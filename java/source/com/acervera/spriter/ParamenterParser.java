/**
 * 
 */
package com.acervera.spriter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author angel
 *
 */
public class ParamenterParser {
	
	@Parameter(names = "-help")
	private boolean help;
	
	@Parameter(names = { "-columns" }, description = "Columns of images in the sprite target file.")
	private int columns = 10;
	
	@Parameter(names = { "-sourceDirectory" }, description = "Source directory where will be searched the images.")
	private String sourceDirectory = ".";
	
	@Parameter(names = { "-targetDirectory" }, description = "Target directory where will be stored generated files.")
	private String targetDirectory = ".";
	
	@Parameter(names = { "-pattern" }, description = "Pattern used to search images." )
	private String pattern = "*.png";
	
	@Parameter(names = { "-targetFileName" }, description = "Image generated." )
	private String targetFileName = "sprite.png";
	
	@Parameter(names = { "-reportFileName" }, description = "Report file name." )
	private String reportFileName = "sprite.txt";
	
	@Parameter(names = { "-cssFileName" }, description = "CSS file name." )
	private String cssFileName = "sprite.css";
	
	@Parameter(names = { "-cssPrefix" }, description = "Style prefix." )
	private String cssPrefix = "span.icon.";
	
	@Parameter(names = { "-targetGrayFileName" }, description = "Image generated in gray scale." )
	private String targetGrayFileName = "sprite-gray.png";
	
	@Parameter(names = { "-groupByDimensions" }, description = "Group images by dimensions." )
	private Boolean groupByDimensions = false;
	
	public ParamenterParser(String[] args) {
		JCommander jCommander = new JCommander(this, args);
		if(isHelp()) {
			jCommander.usage();
		}
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @return the sourceDirectory
	 */
	public String getSourceDirectory() {
		return sourceDirectory;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the targetFileName
	 */
	public String getTargetFileName() {
		return targetFileName;
	}

	/**
	 * @return the help
	 */
	public boolean isHelp() {
		return help;
	}
	
	/**
	 * @return the reportFileName
	 */
	public String getReportFileName() {
		return reportFileName;
	}

	/**
	 * @return the cssFileName
	 */
	public String getCssFileName() {
		return cssFileName;
	}
	
	/**
	 * @return the cssPrefix
	 */
	public String getCssPrefix() {
		return cssPrefix;
	}
	
	/**
	 * @return the targetGrayFileName
	 */
	public String getTargetGrayFileName() {
		return targetGrayFileName;
	}
	
	/**
	 * @return the targetDirectory
	 */
	public String getTargetDirectory() {
		return targetDirectory;
	}
	
	/**
	 * @return the groupByDimensions
	 */
	public Boolean getGroupByDimensions() {
		return groupByDimensions;
	}

	public void display() {
		System.out.println("columns: " + columns);
		System.out.println("sourceDirectory: " + sourceDirectory);
		System.out.println("pattern: " + pattern);
		System.out.println("targetDirectory: " + targetDirectory);
		System.out.println("targetFileName: " + targetFileName);
		System.out.println("reportFileName: " + reportFileName);
		System.out.println("cssFileName: " + cssFileName);
		System.out.println("cssPrefix: " + cssPrefix);
		System.out.println("targetGrayFileName: " + targetGrayFileName);
		System.out.println("groupByDimensions: " + groupByDimensions);
	}
	
}
