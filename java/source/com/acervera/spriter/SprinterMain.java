/**
 * 
 */
package com.acervera.spriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.beust.jcommander.ParameterException;

/**
 * @author angel
 *
 */
public class SprinterMain {
	
	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		try {
			ParamenterParser paramenters = new ParamenterParser(args);
			
			if(!paramenters.isHelp()) {
				paramenters.display();
				
				SprinterService sprinterService = new SprinterService();
				sprinterService.generateSprite(
					paramenters.getColumns(),
					Paths.get(paramenters.getSourceDirectory()),
					Paths.get(paramenters.getTargetDirectory()),
					paramenters.getPattern(),
					paramenters.getTargetFileName(),
					paramenters.getReportFileName(),
					paramenters.getCssFileName(),
					paramenters.getCssPrefix(),
					paramenters.getTargetGrayFileName(),
					paramenters.getGroupByDimensions()
				);
			}
		} catch (ParameterException e) {
			System.err.println("Parameter error. Use -help option to help.");
		}
		
	}

}
