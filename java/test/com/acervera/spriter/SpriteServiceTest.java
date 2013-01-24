/**
 * 
 */
package com.acervera.spriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author angel
 *
 */
public class SpriteServiceTest {
	
	private SprinterService sprinterService = new SprinterService();
	
	@Test
	public void retrievePngFiles() throws IOException, URISyntaxException {
		List<Path> files = sprinterService.retrievePngFiles(Paths.get("./setup/test/filefinder"));
		assert (files.size() == 19):"found " + files.size() + " but must find " + 19;
	}
	
	@Test
	public void retrieveFiles() throws IOException, URISyntaxException {
		List<Path> files = sprinterService.retrieveFiles(Paths.get("./setup/test/filefinder"), "application*.png");
		assert (files.size() == 9):"found " + files.size() + " but must find " + 9;
	}
	
	@Test(dependsOnMethods={"retrievePngFiles"})
	public void calculateMaxSize() throws IOException, URISyntaxException {
		List<Path> files = sprinterService.retrievePngFiles(Paths.get("./setup/test/filefinder"));
		Size size = sprinterService.calculateMaxSize(files);
		assert (size.getHeight() == 30) : "Height must be 30 and is " + size.getHeight();
		assert (size.getWidth() == 16) : "Width must be 16 and is " + size.getWidth();
	}
	
	@Test(dependsOnMethods={"retrieveFiles","calculateMaxSize"})
	public void generateSprite() throws IOException, URISyntaxException {
		List<Path> files = sprinterService.retrieveFiles(Paths.get("./setup/test/filefinder"), "application*.png");
		Size size = sprinterService.calculateMaxSize(files);
		
		Path targetFile = Paths.get("tmp/sprite.png");
		Files.createDirectories(targetFile.getParent());
		
		sprinterService.generateSprite(size, 5, files, targetFile, Paths.get("tmp/sprite.txt"),  Paths.get("tmp/sprite.css"), "span.icon.", Paths.get("tmp/sprite-gray.png"));
	}
	
	@Test
	public void groupByDimension() throws IOException, URISyntaxException {
		
		Map<String, Long> retValues = new HashMap<>();
		retValues.put("16x16", 9L);
		retValues.put("32x32", 4L);
		retValues.put("24x24", 2L);
		
		List<Path> files = sprinterService.retrieveFiles(Paths.get("./setup/test/groupbydimension"), "*.png");
		Map<String, List<Path>> groups = sprinterService.groupByDimension(files);
		
		assert (groups.size() == 3) : "The number of groups must be 3 and is " + groups.size();
		for (String returnedKey : groups.keySet()) {
			Long assertSize = retValues.get(returnedKey);
			assert (assertSize != null) : String.format("Group %1$s not expected", returnedKey);
			
			List<Path> lstGroupedFiles = groups.get(returnedKey);
			assert (!assertSize.equals( lstGroupedFiles.size() )) : String.format("Expected size for group %1$s is %2$s and will be %3$s", returnedKey, assertSize, lstGroupedFiles.size() ); 
		
			retValues.remove(returnedKey);
		}
		
		assert (retValues.size() == 0) : String.format("Expected groups %1$s and did not returned.", retValues);
	}
	


}
