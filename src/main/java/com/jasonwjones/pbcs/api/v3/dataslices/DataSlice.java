package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the data slice response from an Export Data Slice operation
 * 
 * @author jasonwjones
 *
 */
public class DataSlice {

	private List<String> pov;

	private List<List<String>> columns;

	/**
	 * Represents the members and data for a given row (the two lower OLAP grid
	 * quadrants).
	 */
	private List<HeaderDataRow> rows;

	public DataSlice() {
	}

	public DataSlice(List<String> pov, List<List<String>> columns, List<HeaderDataRow> rows) {
		this.pov = pov;
		this.columns = columns;
		this.rows = rows;
	}

	public List<String> getPov() {
		return pov;
	}

	public void setPov(List<String> pov) {
		this.pov = pov;
	}

	/**
	 * Returns the columns (top axis). If there are three dimensions
	 * represented, then the outer list will have three items. Each contained
	 * list will be the elements spanning the entire row.
	 * 
	 * @return the columns
	 */
	public List<List<String>> getColumns() {
		return columns;
	}

	public void setColumns(List<List<String>> columns) {
		this.columns = columns;
	}

	public List<HeaderDataRow> getRows() {
		return rows;
	}

	public void setRows(List<HeaderDataRow> rows) {
		this.rows = rows;
	}

	// TODO: make a general library function
	public DataSlice withMemberReplacement(String currentMember, String newMember) {
		List<List<String>> newColumns = new ArrayList<List<String>>();
		for (List<String> currentColumnList : columns) {
			List<String> newColumn = new ArrayList<String>();
			for (String currentColumnListItem : currentColumnList) {
				if (currentColumnListItem.equals(currentMember)) {
					newColumn.add(newMember);
				} else {
					newColumn.add(currentColumnListItem);
				}
			}
			newColumns.add(newColumn);
		}

		List<HeaderDataRow> newRows = new ArrayList<HeaderDataRow>();
		for (HeaderDataRow row : rows) {
			newRows.add(row.withMemberReplacement(currentMember, newMember));
		}

		return new DataSlice(this.pov, newColumns, newRows);
	}

	public static class HeaderDataRow {

		private List<String> headers;

		private List<String> data;

		public HeaderDataRow() {
		}

		public HeaderDataRow(List<String> headers, List<String> data) {
			this.headers = headers;
			this.data = data;
		}

		public List<String> getHeaders() {
			return headers;
		}

		public void setHeaders(List<String> headers) {
			this.headers = headers;
		}

		public List<String> getData() {
			return data;
		}

		public void setData(List<String> data) {
			this.data = data;
		}

		public HeaderDataRow withMemberReplacement(String currentMember, String newMember) {
			List<String> newHeaders = new ArrayList<String>();
			for (String header : headers) {
				if (header.equals(currentMember)) {
					newHeaders.add(newMember);
				} else {
					newHeaders.add(header);
				}
			}
			return new HeaderDataRow(newHeaders, this.data);
		}

	}

}
