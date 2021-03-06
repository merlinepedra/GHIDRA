/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docking.widgets.table;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import org.junit.Test;

import ghidra.util.task.TaskMonitor;

public class GTableCSVTest {

	@Test
	public void testCsv_QuotesGetEscaped() {

		AnyObjectTableModel<CsvRowObject> model =
			new AnyObjectTableModel<>("MyModel", CsvRowObject.class,
				"getName", "getDescription", "getNumber");

		List<CsvRowObject> data = Arrays.asList(
			new CsvRowObject("Bob", "Bobby", 11),
			new CsvRowObject("Joan", "Joan has \"quoted\" text", 0),
			new CsvRowObject("Sam", "\"Sam has a single quote text", 23),
			new CsvRowObject("Time", "Tim is last", 33));
		model.setModelData(data);

		GTable table = new GTable(model);
		List<Integer> columns = new ArrayList<>();

		PrintWriterSpy writer = new PrintWriterSpy();
		GTableToCSV.writeCSV(writer, table, columns, TaskMonitor.DUMMY);

		assertCsvRowValues(data, writer);
	}

	@Test
	public void testCsv_CommasGetEscaped() {

		AnyObjectTableModel<CsvRowObject> model =
			new AnyObjectTableModel<>("MyModel", CsvRowObject.class,
				"getName", "getDescription", "getNumber");

		List<CsvRowObject> data = Arrays.asList(
			new CsvRowObject("Bob", "Bobby", 11),
			new CsvRowObject("Joan", "Joan has a comma, in her text", 0),
			new CsvRowObject("Sam", ",Sam has a leading comma", 23),
			new CsvRowObject("Time", "Tim is last", 33));
		model.setModelData(data);

		GTable table = new GTable(model);
		List<Integer> columns = new ArrayList<>();

		PrintWriterSpy writer = new PrintWriterSpy();
		GTableToCSV.writeCSV(writer, table, columns, TaskMonitor.DUMMY);

		assertCsvRowValues(data, writer);
	}

	@Test
	public void testCsv_BooleaValues() {

		AnyObjectTableModel<BooleanCsvRowObject> model =
			new AnyObjectTableModel<>("MyModel", BooleanCsvRowObject.class,
				"getName", "isSelected");

		List<BooleanCsvRowObject> data = Arrays.asList(
			new BooleanCsvRowObject("Bob", true),
			new BooleanCsvRowObject("Joan", false),
			new BooleanCsvRowObject("Sam", false),
			new BooleanCsvRowObject("Time", true));
		model.setModelData(data);

		GTable table = new GTable(model);
		List<Integer> columns = new ArrayList<>();

		PrintWriterSpy writer = new PrintWriterSpy();
		GTableToCSV.writeCSV(writer, table, columns, TaskMonitor.DUMMY);

		assertBooleanCsvRowValues(data, writer);
	}

	private void assertCsvRowValues(List<CsvRowObject> data, PrintWriterSpy writer) {

		String results = writer.toString();
		String[] lines = results.split("\n");
		for (int i = 1; i < lines.length; i++) {
			int index = i - 1; // the first line is the header
			CsvRowObject row = data.get(index);
			String line = lines[i];
			String[] columns = line.split("(?<!\\\\),");

			String name = columns[0].replaceAll("\\\\,", ",");
			name = name.replaceAll("\\\\\"", "\"");
			assertEquals("\"" + row.getName() + "\"", name);

			String description = columns[1].replaceAll("\\\\,", ",");
			description = description.replaceAll("\\\\\"", "\"");
			assertEquals("\"" + row.getDescription() + "\"", description);

			String number = columns[2].replaceAll("\\\\,", ",");
			number = number.replaceAll("\\\\\"", "\"");
			assertEquals("\"" + row.getNumber() + "\"", number);
		}
	}

	private void assertBooleanCsvRowValues(List<BooleanCsvRowObject> data, PrintWriterSpy writer) {

		String results = writer.toString();
		String[] lines = results.split("\n");
		for (int i = 1; i < lines.length; i++) {
			int index = i - 1; // the first line is the header
			BooleanCsvRowObject row = data.get(index);
			String line = lines[i];
			String[] columns = line.split("(?<!\\\\),");

			String name = columns[0].replaceAll("\\\\,", ",");
			name = name.replaceAll("\\\\\"", "\"");
			assertEquals("\"" + row.getName() + "\"", name);

			String isSelected = columns[1];
			assertEquals("\"" + row.isSelected() + "\"", isSelected);
		}
	}

	class CsvRowObject {

		private String name;
		private String description;
		private int number;

		CsvRowObject(String name, String description, int number) {
			this.name = name;
			this.description = description;
			this.number = number;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public int getNumber() {
			return number;
		}
	}

	class BooleanCsvRowObject {

		private String name;
		private boolean isSelected;

		BooleanCsvRowObject(String name, boolean isSelected) {
			this.name = name;
			this.isSelected = isSelected;
		}

		public String getName() {
			return name;
		}

		public boolean isSelected() {
			return isSelected;
		}
	}

	private class PrintWriterSpy extends PrintWriter {

		private StringWriter stringWriter;

		PrintWriterSpy() {
			super(new StringWriter(), true);
			stringWriter = ((StringWriter) out);
		}

		@Override
		public String toString() {
			return stringWriter.toString();
		}
	}
}
