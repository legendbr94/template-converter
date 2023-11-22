package com.api.templateconverter.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompactacaoUtil {

	private CompactacaoUtil(){
	}
	
	private static byte[] reverterBase64(String textoCompactadoBase64) {
		return Base64.getDecoder().decode(textoCompactadoBase64);
	}

	public static String reverterBase64Descompactar(String stringParaReverter) throws IOException {
		byte[] htmlCompactadoByte = reverterBase64(stringParaReverter);
		return descompactarString(htmlCompactadoByte);
	}
	
	public static String compactarTransformarBase64(byte[] bytesParaTransformar) throws IOException {
		byte[] htmlCompactado = compactar(bytesParaTransformar);
		return gerarFormatacaoEnvio(htmlCompactado);
	}
	
	public static byte[] compactar(byte[] input) throws IOException {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
			GZIPOutputStream gzip = new GZIPOutputStream(baos)){
			gzip.write(input);
			gzip.close();
			return baos.toByteArray();
		}
	}
	
	private static String gerarFormatacaoEnvio(byte[] textoCompactado) {
		return (new String(Base64.getEncoder().encode(textoCompactado))).replace("\n", "");
	}
	
	private static String descompactarString(byte[] textoCompactado) throws IOException {
		return new String(descompactar(textoCompactado),StandardCharsets.UTF_8);

	}

	private static byte[] descompactar(byte[] textoCompactado) throws IOException {
		try (
				InputStream input = new ByteArrayInputStream(textoCompactado);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPInputStream gzip = new GZIPInputStream(input);
		){
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gzip.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toByteArray();
		}
	}
}
