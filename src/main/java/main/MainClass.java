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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
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

    private static final List<BinanceTx> binanceTxList = new ArrayList<>();
    private static final Map<String, BigDecimal> gains = new HashMap<>();
    private static List<Transaction> coinbaseTxList = new ArrayList<>();

    public static void main(String[] args) {
        LOGGER.info("----- Coinbase -----");
        readCoinbaseCsv();

        coinbaseTxList.stream()
            .sorted()
            .forEach(tx -> LOGGER.info(tx.toString()));

        getCoinbaseAirdrops(2021);

        LOGGER.info("----- Binance -----");
        readBinanceBuyHistory();

        readBinanceSpot();

        printBinanceTxs();

        calcGains();
    }

    private static void calcGains() {
        // Filter SOL transactions from binance list
        binanceTxList.stream()
            .sorted()
            .collect(groupingBy(BinanceTx::getPair, groupingBy(BinanceTx::getTransactionType,
                Collectors.toCollection(LinkedList::new))))
            .forEach((pair, txList) -> {
                var gain = calcGains(pair, txList);
                gains.put(pair, gain);
            });

        var totalGains = gains.values().stream().mapToDouble(BigDecimal::doubleValue).sum();

        LOGGER.info("Total gains: {}", totalGains);
    }

    private static BigDecimal calcGains(String pair,
        Map<EnumTransactionType, LinkedList<BinanceTx>> txs) {
        var buys = txs.get(EnumTransactionType.BUY);
        var sells = txs.get(EnumTransactionType.SELL);

        BigDecimal totalProfit = gains(sells, buys);

        LOGGER.info("{} gains: {}", pair, totalProfit);

        return totalProfit;
    }

    private static BigDecimal gains(LinkedList<BinanceTx> sells, LinkedList<BinanceTx> buys) {
        var profit = BigDecimal.ZERO;

        if (sells == null || buys == null) {
            return BigDecimal.ZERO;
        }
        if (buys.isEmpty() && !sells.isEmpty()) {
            LOGGER.error("Quedan ventas realizadas pero no se han realizado compras");
        } else if (!buys.isEmpty() && sells.isEmpty()) {
            LOGGER.info("Se han terminado de procesar las ventas");
            // TODO: calcular cantidad de moneda restante
        } else if (buys.isEmpty()) {
            LOGGER.info("Tanto compras como ventas han terminado");
        } else {
            var buy = buys.element();
            var sell = sells.element();

            var amountPurchased = buy.getExecuted();
            var amountSold = sell.getExecuted();
            LOGGER.debug("Buy: {}", amountPurchased);

            LOGGER.debug("Sell: {}", amountSold);

            if (amountSold.compareTo(amountPurchased) == 0) {
                profit = profit.add(
                    calculateSellGains(amountSold, buy.getPrice(), sell.getPrice()));
                sells.pop();
                buys.pop();
            }
            if (amountSold.compareTo(amountPurchased) < 0) {
                profit = profit.add(
                    calculateSellGains(amountSold, buy.getPrice(), sell.getPrice()));
                buy.setExecuted(amountPurchased.subtract(amountSold));
                sells.pop();
            } else {
                profit = profit.add(
                    calculateSellGains(amountPurchased, buy.getPrice(), sell.getPrice()));
                sell.setExecuted(amountSold.subtract(amountPurchased));
                buys.pop();
            }

            if (profit.compareTo(BigDecimal.ZERO) > 0) {
                profit = convert("USD", "EUR", sell.getDate(), profit);
                profit = profit.add(gains(sells, buys));
            }
        }

        return profit;
    }

    private static BigDecimal calculateSellGains(Currency amountSold,
        BigDecimal buyPrice, BigDecimal sellPrice) {
        var purchaseCost = amountSold.multiply(buyPrice);
        var salesGain = amountSold.multiply(sellPrice);
        return salesGain.getAmount().subtract(purchaseCost.getAmount());
    }

    private static void printBinanceTxs() {
        var txByTxType = binanceTxList
            .stream()
            .sorted()
            .collect(groupingBy(BinanceTx::getPair));

        txByTxType.forEach((txType, txList) -> {
            LOGGER.info("Pair: {}", txType);
            txList.forEach(tx -> LOGGER.info(tx.toString()));
        });
    }

    private static void readBinanceBuyHistory() {
        try {
            var txs = listFilesByExt(TX_BINANCE_BUY_FOLDER, ".xlsx")
                .stream()
                .map(MainClass::readBinanceBuyHistoryFile)
                .flatMap(List::stream)
                .toList();

            binanceTxList.addAll(txs);
        } catch (IOException e) {
            LOGGER.error("Error reading files", e);
        }
    }

    private static List<BinanceTx> readBinanceBuyHistoryFile(String file) {
        List<BinanceTx> result = new ArrayList<>();
        try (var workbook = new XSSFWorkbook(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);

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
                    var cell = row.getCell(0);
                    BinanceTx tx = buildBinanceTx(zoneId, row, cell);
                    if (tx != null) {
                        result.add(tx);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error reading file", e);
        }
        return result;
    }

    private static BinanceTx buildBinanceTx(String zoneId, Row row, Cell cell) {
        BinanceTx tx = null;
        // Date
        var date = LocalDateTime.parse(cell.getStringCellValue(), formatter);
        // Method
        var method = row.getCell(1).getStringCellValue();
        // Amount
        var amount = row.getCell(2).getStringCellValue();
        // Price
        var price = row.getCell(3).getStringCellValue();
        // Fees
        var fees = row.getCell(4).getStringCellValue();
        // Final Amount
        var finalAmount = row.getCell(5).getStringCellValue();
        // Status
        var status = row.getCell(6).getStringCellValue();
        // Transaction ID
        var txId = row.getCell(7).getStringCellValue();

        if ("Completed".equalsIgnoreCase(status)) {
            tx = new BinanceTx();
            tx.setDate(date.atZone(ZoneId.of(zoneId)));
            var priceTokens = price.split(" ");
            tx.setPrice(new BigDecimal(priceTokens[0]));
            var pair = priceTokens[1].split("/");
            tx.setPair(pair[0] + pair[1]);
            tx.setTransactionType(EnumTransactionType.BUY);
            var finalAmountTokens = finalAmount.split(" ");
            tx.setExecuted(
                new Currency(new BigDecimal(finalAmountTokens[0]), finalAmountTokens[1]));
            var amountTokens = amount.split(" ");
            tx.setAmount(new Currency(new BigDecimal(amountTokens[0]), amountTokens[1]));
            var feesTokens = fees.split(" ");
            tx.setFee(new Currency(new BigDecimal(feesTokens[0]), feesTokens[1]));
            tx.setTransactionId(txId);
            tx.setMethod(method);
        }

        return tx;
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

            binanceTxList.addAll(txs);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static List<BinanceTx> readBinanceCsv(String file) {
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
            coinbaseTxList = listFilesByExt(TX_COINBASE_FOLDER, ".csv")
                .stream()
                .map(MainClass::readCoinbaseTransactions)
                .flatMap(List::stream)
                .map(Transaction::new)
                .toList();
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

    private static void getCoinbaseAirdrops(int year) {
        var airdropTxStream = coinbaseTxList.stream()
            .filter(tx -> tx.getTransactionType() == EnumTransactionType.AIRDROP
                && tx.getDate().getYear() == year);

        var airdropByAsset =
            airdropTxStream
                .collect(
                    groupingBy(tx -> tx.getExecuted().getCoin(),
                        Collectors.summingDouble(convertTx())));

        airdropByAsset.forEach((asset, total) -> LOGGER.info("Asset: {}, Total: {}", asset, total));

        var total = (Double) airdropByAsset.values().stream().mapToDouble(v -> v).sum();

        LOGGER.info("Ganancias en Airdrops: {}", total);
    }

    private static ToDoubleFunction<Transaction> convertTx() {
        return tx -> convert("USD", "EUR", tx.getDate(),
            tx.getSubTotal().getAmount()).doubleValue();
    }

    private static BigDecimal convert(String from, String to, ZonedDateTime date,
        BigDecimal amount) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format(BASE_URL, from, to, date, amount)))
            .GET()
            .build();

        BigDecimal result = BigDecimal.ZERO;
        try {
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            JsonObject body = new Gson().fromJson(response.body(), JsonObject.class);
            result = body.get("result").getAsBigDecimal();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error converting currency", e);
        }
        return result;
    }

}
