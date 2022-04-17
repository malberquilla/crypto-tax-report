# Cálculo de tasas para la declaración de la renta

Este repo es para pruebas de concepto para el cálculo de las ganancias realizadas de la inversión en
cripto.

Hay que separar 3 conceptos:

1. Compra/venta y permutas
2. Staking y similares
3. Airdrop y similares

El proyecto ha sido creado con IntelliJ Community Edition, usando java 17 y Gradle 7.4.2.

Para el parseo de los csv se ha usado la librería OpenCSV.
Para el parseo de los xlsx se ha usado la librería Apache POI.

## Plataformas

Es necesario establecer la constante `TX_ROOT_FOLDER` con el path absoluto del directorio donde se
almacenarán los reportes.

### Coinbase

Es necesario descargar el reporte de transacciones de Coinbase y guardarlo en la
carpeta `TX_ROOT_FOLDER\Coinbase`.

Por ahora:

1. Recopila las transacciones tipo `Coinbase Earn`
2. Convierte de `USD` a `EUR` tomando el valor de intercambio del
   API `https://api.exchangerate.host`. [+info](https://exchangerate.host/#/)
3. Agrupa por criptomoneda
4. Imprime por pantalla el total de cada criptomoneda y la suma de todas.

### Binance

Dentro de `TX_ROOR_FOLDER` debe existir una carpeta `Binance`. En este caso hay que separar los
distintos tipos de reportes.

| Tipo de reporte     | Carpeta      |
|---------------------|--------------|
| Compras con tarjeta | Binance\Buy  |
| Compras en spot     | Binance\Spot |

Para las compras con tarjeta, es importante solo exportar solo las completadas. Más adelante será el
script el que haga dicho filtro. De momento solo captura la fecha e imprime las transacciones.

Para las compras en spot, lee los ficheros e imprime las transacciones.