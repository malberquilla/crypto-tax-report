package main;

import static java.util.stream.Collectors.groupingBy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClass {

    public static final Logger LOGGER = LoggerFactory.getLogger(MainClass.class);
    private static final HttpClient httpClient = HttpClient.newBuilder()
        .version(Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    private static final String TX_ROOT_PATH = "D:\\Documentos\\Familia\\Crypto\\2021";
    private static final String TX_BINANCE_FOLDER = TX_ROOT_PATH + "\\Binance";
    private static final String TX_BINANCE_BUY_FOLDER = TX_BINANCE_FOLDER + "\\Buy";
    private static final String TX_BINANCE_SPOT_FOLDER = TX_BINANCE_FOLDER + "\\Spot";
    private static final String TX_COINBASE_FOLDER = TX_ROOT_PATH + "\\Coinbase";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss");

    private static final String BASE_URL = "https://api.exchangerate.host/convert?from=%s&to=%s&date=%s&amount=%s";

    public static void main(String[] args) {
        LOGGER.info("----- Coinbase -----");
        readCoinbaseCsv();

        LOGGER.info("----- Binance -----");
        readBinanceBuyHistory();

        readBinanceSpot();
    }

    private static void readBinanceBuyHistory() {
        try {
            listFilesByExt(TX_BINANCE_BUY_FOLDER, ".xlsx")
                .stream()
                .map(MainClass::readBinanceBuyHistoryFile)
                .forEach(f -> LOGGER.info("Reading file {}", f));
        } catch (IOException e) {
            LOGGER.error("Error reading files", e);
        }
    }

    private static List<BinanceTx> readBinanceBuyHistoryFile(String file) {
        List<BinanceTx> result = new ArrayList<>();
        try (var workbook = new XSSFWorkbook(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);

            BinanceTx tx;
            String zoneId = "UTC";
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Get date format
                    var cell = row.getCell(0);
                    Matcher m = Pattern.compile("\\((.*?)\\)").matcher(cell.getStringCellValue());
                    if (m.find()) {
                        zoneId = m.group(1);
                    }
                } else {
                    tx = new BinanceTx();

                    // Date
                    var cell = row.getCell(0);
                    var date = LocalDateTime.parse(cell.getStringCellValue(), formatter);
                    tx.setDate(date.atZone(ZoneId.of(zoneId)));

                    result.add(tx);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error reading file", e);
        }
        return result;
    }

    private static Set<String> listFilesByExt(String folder, String ext) throws IOException {
        try (var stream = Files.list(Paths.get(folder))) {
            return stream
                .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(ext))
                .map(Path::toString)
                .collect(Collectors.toSet());
        }
    }

    private static void readBinanceSpot() {
        try {
            var txs = listFilesByExt(TX_BINANCE_SPOT_FOLDER, ".csv")
                .stream()
                .map(MainClass::readBinanceCsv)
                .flatMap(List::stream)
                .toList();

            var txByTxType = txs
                .stream()
                .sorted()
                .collect(groupingBy(BinanceTx::getPair));

            txByTxType.forEach((txType, txList) -> {
                LOGGER.info("Transaction type: {}", txType);
                txList.forEach(tx -> LOGGER.info(tx.toString()));
            });

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static <R> List<BinanceTx> readBinanceCsv(String file) {
        LOGGER.info("Reading file: {}", file);
        try {
            return new CsvToBeanBuilder<BinanceTx>(
                new FileReader(file))
                .withType(BinanceTx.class)
                .build()
                .parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void readCoinbaseCsv() {
        try {
            var txs = listFilesByExt(TX_COINBASE_FOLDER, ".csv")
                .stream()
                .map(MainClass::readCoinbaseTransactions)
                .flatMap(List::stream)
                .toList();

            var txByTxType = txs
                .stream()
                .sorted()
                .collect(groupingBy(CoinbaseTx::getTransactionType));

            sumTransactionsByYear(txByTxType.get(EnumTransactionType.AIRDROP), 2021);
        } catch (IOException e) {
            LOGGER.error("Error reading files", e);
        }
    }

    private static List<CoinbaseTx> readCoinbaseTransactions(String file) {
        LOGGER.info("Reading file: {}", file);
        try {
            return new CsvToBeanBuilder<CoinbaseTx>(
                new FileReader(file))
                .withSkipLines(7)
                .withType(CoinbaseTx.class)
                .build()
                .parse();
        } catch (FileNotFoundException e) {
            LOGGER.error("Error reading file", e);
            throw new RuntimeException(e);
        }
    }

    private static void sumTransactionsByYear(List<CoinbaseTx> coinbaseTxList, int year) {

        var airdropTxStream = coinbaseTxList.stream()
            .filter(tx -> tx.getTransactionType() == EnumTransactionType.AIRDROP
                && tx.getTimestamp().getYear() == year);

        var airdropByAsset =
            airdropTxStream
                .collect(
                    groupingBy(CoinbaseTx::getAsset,
                        Collectors.summingDouble(convertTx())));

        airdropByAsset.forEach((asset, total) -> LOGGER.info("Asset: {}, Total: {}", asset, total));

        var total = (Double) airdropByAsset.values().stream().mapToDouble(v -> v).sum();

        LOGGER.info("Ganancias en Airdrops: {}", total);
    }

    private static ToDoubleFunction<CoinbaseTx> convertTx() {
        return tx -> convert("USD", "EUR", tx.getTimestamp(), tx.getTotal());
    }

    private static double convert(String from, String to, ZonedDateTime date, BigDecimal amount) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format(BASE_URL, from, to, date, amount)))
            .GET()
            .build();

        double result = 0;
        try {
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            JsonObject body = new Gson().fromJson(response.body(), JsonObject.class);
            result = body.get("result").getAsDouble();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error converting currency", e);
        }
        return result;
    }

}
