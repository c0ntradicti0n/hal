package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;

import model.CSVPoint;
import model.ClusterCenter;

public class CsvReader {

	public static List<ClusterCenter> readClusterCenters(String path) {
		new File(path);
		System.out.println(path);
		List<ClusterCenter> beans = null;
		try {
			beans = new CsvToBeanBuilder(new FileReader(path)).withSeparator('\t').withType(ClusterCenter.class).build()
					.parse();
		} catch (IllegalStateException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return beans;
	}
	public static List<CSVPoint> readCSVPoints(String path) {
		System.out.println("reading " + path);
		new File(path);
		List<CSVPoint> beans = null;
		try {
			beans = new CsvToBeanBuilder(new FileReader(path)).withSeparator('\t').withType(CSVPoint.class).build()
					.parse();
		} catch (IllegalStateException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return beans;
	}
}
