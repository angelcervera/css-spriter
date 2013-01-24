/**
 * 
 */
package com.acervera.spriter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * @author angel
 *
 */
public class SprinterService {
	
	public void generateSprite(int columns, Path sourceDirectory, Path targetDirectory, String pattern, String targetFileName, String reportFileName, String cssFileName, String cssPrefix, String targetGrayFileName, Boolean groupByDimensions) throws IOException, URISyntaxException {
		List<Path> files = retrieveFiles(sourceDirectory, pattern);
		
		System.out.println("Found " + files.size() + " files.");
		
		if(groupByDimensions) {
			Map<String, List<Path>> groups = groupByDimension(files);
			for (String key : groups.keySet()) {
				List<Path> groupedFiles = groups.get(key);
				Size size = calculateMaxSize(groupedFiles);
				Path groupTargetDirectory = Paths.get(targetDirectory.toString(), key);
				Path targetFile = Paths.get(groupTargetDirectory.toString() , targetFileName);
				Path reportFile = Paths.get(groupTargetDirectory.toString() , reportFileName);
				Path cssFile = Paths.get(groupTargetDirectory.toString() , cssFileName);
				Path targetGrayFile = Paths.get(groupTargetDirectory.toString() , targetGrayFileName);
				Files.createDirectories(groupTargetDirectory);
				generateSprite(size, columns, groupedFiles, targetFile, reportFile, cssFile, cssPrefix, targetGrayFile);	
			}
		} else {
			Size size = calculateMaxSize(files);
			Path targetFile = Paths.get(targetDirectory.toString(), targetFileName);
			Path reportFile = Paths.get(targetDirectory.toString(), reportFileName);
			Path cssFile = Paths.get(targetDirectory.toString(), cssFileName);
			Path targetGrayFile = Paths.get(targetDirectory.toString(), targetGrayFileName);
			generateSprite(size, columns, files, targetFile, reportFile, cssFile, cssPrefix, targetGrayFile);	
		}

	}
	
	/**
	 * Generate Sprite image file.
	 * 
	 * @param size
	 * @param columns
	 * @param files
	 * @param targetFile
	 * @param reportFile
	 * @param cssFile
	 * @param cssPrefix
	 * @throws IOException
	 */
	protected void generateSprite(Size size, int columns, List<Path> files, Path targetFile, Path reportFile, Path cssFile, String cssPrefix, Path targetGrayFile) throws IOException {
		
		int width = columns*size.getWidth();
		int rows = files.size() / columns;
		if(files.size() % columns > 0) {
			rows ++;
		}
		int height = rows * size.getHeight();
		
		Files.deleteIfExists(reportFile);
		Files.deleteIfExists(cssFile);
		Charset charset = Charset.forName("UTF-8");
		try(
				BufferedWriter reportBF = Files.newBufferedWriter(reportFile, charset, StandardOpenOption.CREATE);
				BufferedWriter cssBF = Files.newBufferedWriter(cssFile, charset, StandardOpenOption.CREATE);
		) {
			
			BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
			
			int column=0, x=0, y=0;
			for(Path path : files){
				try(
					InputStream is = Files.newInputStream(path);
				) {
					Graphics graphics = result.getGraphics();
			        BufferedImage bi = ImageIO.read(is);
			        graphics.drawImage(bi, x, y, null);
			        
			        reportBF.write("Filename: " + path.getFileName() + " Position: ["+x+","+y+"] Dimensions: ["+bi.getWidth()+","+bi.getHeight()+"]");
			        reportBF.newLine();
			        
			        String name = path.getFileName().toString();
			        if(name.lastIndexOf('.') > 0) {
			        	name = name.substring( 0, name.lastIndexOf('.') );
			        }
			        cssBF.write(cssPrefix + name + " {");
			        cssBF.newLine();
			        cssBF.write("\tbackground-position : -"+x+"px -"+y+"px;");
			        cssBF.newLine();
			        cssBF.write("\tline-height : "+bi.getHeight()+"px;");
			        cssBF.newLine();
			        cssBF.write("\theight : "+bi.getHeight()+"px;");
			        cssBF.newLine();
			        cssBF.write("\twidth : "+bi.getWidth()+"px;");
			        cssBF.newLine();
			        cssBF.write("\tmax-width : "+bi.getWidth()+"px;");
			        cssBF.newLine();
			        cssBF.write("}");
			        cssBF.newLine();
			        cssBF.newLine();
			        
			        column++;
			        if(column >= columns) {
			        	column=0;
			        	x = 0;
			        	y += size.getHeight();
			        } else {
			        	x += size.getWidth();
			        }
				}
		    }
			
			Files.deleteIfExists(targetFile);
			try(
				OutputStream os = Files.newOutputStream(targetFile);
			) {
				ImageIO.write(result,"png", os);
			}
			
			if(targetGrayFile != null) {
				try(
					OutputStream os = Files.newOutputStream(targetGrayFile);
				) {
					
					BufferedImage grayResult = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_GRAY);
					Graphics g = grayResult.getGraphics();
					g.drawImage(result, 0, 0, null);
					g.dispose();
					
					int color = grayResult.getRGB(0, 0);
			        Image imgTransparent = makeColorTransparent(grayResult, new Color(color));
			        BufferedImage grayResultTransparent = imageToBufferedImage(imgTransparent, width, height );
					
					ImageIO.write(grayResultTransparent,"png", os);
				}

			}
		}

	}
	
	/**
	 * Solution by http://stackoverflow.com/a/665483/248304
	 * 
	 * @param im
	 * @param color
	 * @return
	 */
	public static Image makeColorTransparent(BufferedImage im, final Color color) {
		ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
	
	private BufferedImage imageToBufferedImage(Image image, int width, int height)
	{
		BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return dest;
	}

	/**
	 * Calculate max dimensions of a list of files.
	 * 
	 * @param files
	 * @return
	 * @throws IOException 
	 */
	protected Size calculateMaxSize(List<Path> files) throws IOException {
		
		Size size = new Size();
		for (Path path : files) {
			try(
					InputStream is = Files.newInputStream(path);
			) {
				BufferedImage bi = ImageIO.read(is);
				if(bi.getHeight() > size.getHeight()) {
					size.setHeight(bi.getHeight());
				}
				if(bi.getWidth() > size.getWidth()) {
					size.setWidth(bi.getWidth());
				}
			}
		}
		
		return size;
	}
	
	/**
	 * Group by dimension.
	 * 
	 * @param files
	 * @return
	 * @throws IOException
	 */
	protected Map<String, List<Path>> groupByDimension(List<Path> files) throws IOException {
		Map<String, List<Path>> retValue = new HashMap<>();
		
		for (Path path : files) {
			try(
					InputStream is = Files.newInputStream(path);
			) {
				BufferedImage bi = ImageIO.read(is);
				
				String key = bi.getWidth() + "x" + bi.getHeight();
				List<Path> lstFilesByDimension = retValue.get(key);
				if(lstFilesByDimension == null) {
					lstFilesByDimension = new ArrayList<>();
					retValue.put(key, lstFilesByDimension);
				}
				lstFilesByDimension.add(path);
			}
		}
		
		return retValue;
	}
	
	protected List<Path> retrievePngFiles(Path directory) throws IOException, URISyntaxException {
        FileFinder finder = new FileFinder();
        Files.walkFileTree(directory, finder);
        return finder.getLstFiles();
	}
	
	protected List<Path> retrieveFiles(Path directory, String pattern) throws IOException, URISyntaxException {
        FileFinder finder = new FileFinder(pattern);
        Files.walkFileTree(directory, finder);
        return finder.getLstFiles();
	}
}
