/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.math.BigInteger;

/**
 *
 * @author gr0v3r
 */
public class CUF_CodigoUnicodeFactura {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //String cuf = obtenerCUF("1234567891234", "20190113163721242", 0, 2, 1, 1, 10, 10, 0);
        //System.out.println(cuf);
        
        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("example/PruebasCUF.csv").toURI()))){
            stream.forEach((String line) -> {
                String[] arr = line.split(",");
                String cuf = obtenerCUF(arr[1],
                        arr[2],
                        Integer.valueOf(arr[3]),
                        Integer.valueOf(arr[4]),
                        Integer.valueOf(arr[5]),
                        Integer.valueOf(arr[6]),
                        Integer.valueOf(arr[7]),
                        Integer.valueOf(arr[8]),
                        Integer.valueOf(arr[9]));
                System.out.println(arr[0] + ".- " + cuf + " = " + arr[22] + " -> " + (cuf.equals(arr[22])));
            });
        } catch (IOException | URISyntaxException e) {
            System.err.println(e.getMessage());
        }

        /*try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("com.gr0v3r.CUF_CodigoUnicodeFactura/PruebasCUF.csv").toURI()))) {
            stream.forEach((String line) -> {
                String[] arr = line.split(",");
                String cuf = obtenerCUF(arr[1],
                        arr[2],
                        Integer.valueOf(arr[3]),
                        Integer.valueOf(arr[4]),
                        Integer.valueOf(arr[5]),
                        Integer.valueOf(arr[6]),
                        Integer.valueOf(arr[7]),
                        Integer.valueOf(arr[8]),
                        Integer.valueOf(arr[9]));
                System.out.println(
                        arr[0] + ".- " + cuf + " = " + arr[22] + " -> " + (cuf.equals(arr[22])));
            });
        } catch (IOException | URISyntaxException e) {
            System.err.println(e.getMessage());
        }*/
    }
    
    /**
     * @param nit NIT emisor
     * @param fh Fecha y Hora en formato yyyyMMddHHmmssSSS
     * @param sucursal
     * @param mod Modalidad
     * @param temision Tipo de Emision
     * @param cdf Codigo Documento Fiscal
     * @param tds Tipo Documento Sector
     * @param nf Numero de Factura
     * @param pos Punto de Venta
     * @return CUF Codigo Unico de Factura
     */
    public static String obtenerCUF(String nit, String fh, int sucursal, int mod, int temision, int cdf, int tds, int nf, int pos) {
        String cadena = "";

        /**
         * PASO 1 y PASO2 Completa con ceros cada campo y concatena todo en una
         * sola cadena
         */
        cadena += String.format("%013d", new BigInteger(nit));
        cadena += fh;
        cadena += String.format("%04d", sucursal);
        cadena += mod;
        cadena += temision;
        cadena += cdf;
        cadena += String.format("%02d", tds);
        cadena += String.format("%08d", nf);
        cadena += String.format("%04d", pos);
        
        /**
         * Paso 3 Obtiene modulo 11 y adjunta resultado a la cadena
         */
        String mod11 = calculaDigitoMod11(cadena, 1, 9, false);
        cadena += mod11;
        
        /**
         * paso 4 Aplica base16
         */
        BigInteger cuf = new BigInteger(cadena);
        return cuf.toString(16).toUpperCase();
    }

    /**
     * @see https://impuestos.gob.bo/ ALGORITMO BASE 11 – MÓDULO 11
     */
    public static String calculaDigitoMod11(String dado, int numDig, int limMult, boolean x10) {
        int mult, soma, i, n, dig;
        if (!x10) {
            numDig = 1;            
        }       
        for (n = 1; n <= numDig; n++) {            
            soma = 0;
            mult = 2;
            for (i = dado.length() - 1; i >= 0; i--) {
                soma += (mult * Integer.parseInt(dado.substring(i, i + 1)));                                
                if (++mult > limMult) {
                    mult = 2;                    
                }
            }
            if (x10) {
                dig = ((soma * 10) % 11) % 10;                
            } else {
                dig = soma % 11;                
            }
            
            if (dig == 10) {
                dado
                        += "1";
            }
            if (dig == 11) {
                dado
                        += "0";
            }
            if (dig < 10) {
                dado += String.valueOf(dig);
            }
        }    
        return dado.substring(dado.length() - numDig, dado.length());
    }
    
}
