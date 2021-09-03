package utils;

import com.thoughtworks.gauge.screenshot.CustomScreenshotWriter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.driver.Driver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class CustomScreenshotGrabber implements CustomScreenshotWriter {

	@Override
	public String takeScreenshot() {
		TakesScreenshot driver = (TakesScreenshot) Driver.driver;
		String screenshotFileName = String.format("screenshot-%s.png", UUID.randomUUID().toString());
		File screenshotFile = new File(Paths.get(System.getenv("gauge_screenshots_dir"), screenshotFileName).toString());
		File tmpFile = driver.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(tmpFile, screenshotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return screenshotFileName;
	}

}