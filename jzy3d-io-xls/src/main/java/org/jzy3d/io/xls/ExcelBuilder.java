package org.jzy3d.io.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFAnchor;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
// import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * A utility wrapper around Apache POI Excel spreadsheet builder.
 *
 * Comments only supported on XLS type (no XLSX)
 *
 * To add new excel features, see
 *
 * @see http://poi.apache.org/spreadsheet/quick-guide.html (or a copy in /doc)
 * @see http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/xssf/usermodel/examples/
 */
public class ExcelBuilder implements IExcelBuilder {
  static Log log = LogFactory.getLog(ExcelBuilder.class);

  /**
   * The max number of columns supported by an excel sheet (256="IV" column header)
   */
  public static int MAX_COLUMN = 256;

  /** The max number of rows supported by an excel sheet */
  public static int MAX_ROW = 65536;

  public static int LAST_COLUMN = MAX_COLUMN - 1;

  public static int LAST_ROW = MAX_ROW - 1;

  /**
   * States if the builder should throw an IllegalArgumentException or simply warn with a log once a
   * cell index exceed max number of columns or row.
   */
  public static boolean CRASH_ON_CELL_OVERFLOW = false;

  public enum Type {
    XLS, XLSX
  }

  // factories and content
  protected Type type;

  protected Workbook workbook;

  protected CreationHelper create;

  protected Drawing drawing;

  protected Font boldFont;

  protected int currentSheetId;

  // indexs
  protected Map<Integer, Sheet> sheets;

  public ExcelBuilder() {
    this(Type.XLS);
  }

  public ExcelBuilder(Type type) {
    this(type, "all");
  }

  public ExcelBuilder(Type type, String firstSheetName) {
    this(type, firstSheetName, Type.XLSX.equals(type) ? new XSSFWorkbook() : new HSSFWorkbook());
  }

  public ExcelBuilder(Type type, String firstSheetName, Workbook workbook) {
    this.type = type;
    this.workbook = workbook;
    this.sheets = new HashMap<Integer, Sheet>();
    this.create = workbook.getCreationHelper();
    this.currentSheetId = newSheet(0, firstSheetName);
    this.drawing = getCurrentSheet().createDrawingPatriarch();

    this.boldFont = workbook.createFont();
    // this.boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
  }

  public ExcelBuilder(String workbookFile) throws IOException {
    this(load(workbookFile));
  }
  
  public ExcelBuilder(Workbook workbook) {
  	this.workbook = workbook;
  	this.currentSheetId = 0;
	  
    this.sheets = new HashMap<Integer, Sheet>();
    
    for (int i=0; i<workbook.getNumberOfSheets(); i++) {
    	sheets.put(i, workbook.getSheetAt(i));
    }
    
    this.create = workbook.getCreationHelper();
    this.drawing = getCurrentSheet().createDrawingPatriarch();
    this.boldFont = workbook.createFont();
  }

  /** {@inheritDoc} */
  @Override
  public Workbook getWorkbook() {
    return workbook;
  }

  public HSSFWorkbook getHSSFWorkbook() {
    return (HSSFWorkbook) workbook;
  }

  public XSSFWorkbook getXSSFWorkbook() {
    return (XSSFWorkbook) workbook;
  }

  /* CELL MANAGEMENT */

  /** {@inheritDoc} */
  @Override
  public Cell setCell(int row, int column, String content, CellStyle style) {
    if (!validateCellIndex(row, column, content)) {
      // do not try to create an invalid cell
      return null;
    }

    Cell cell = getOrCreateCell(row, column);
    cell.setCellValue(create.createRichTextString(content));

    if (style != null) {
      cell.setCellStyle(style);
    }
    return cell;
  }

  /**
   * Validate a cell index.
   *
   * If cell index is out of maximum number of rows/columns:
   * <ul>
   * <li>throws an {@link IllegalArgumentException} if {@link CRASH_ON_CELL_OVERFLOW} is set to
   * true.
   * <li>otherwise emit a log warning and return false to invalidate this cell.
   * </ul>
   */
  protected boolean validateCellIndex(int row, int column, String content) {
    if (row >= MAX_ROW) {
      String message =
          "max number of row (" + MAX_ROW + ") exceeded @ " + row + " by '" + content + "'";
      if (CRASH_ON_CELL_OVERFLOW)
        throw new IllegalArgumentException(message);
      else
        log.warn(message);
      return false;
    }
    if (column >= MAX_COLUMN) {
      String message = "max number of column (" + MAX_COLUMN + ") exceeded @ " + column + " by '"
          + content + "'";
      if (CRASH_ON_CELL_OVERFLOW)
        throw new IllegalArgumentException(message);
      else
        log.warn(message);
      return false;
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Cell setCell(int row, int column, String content) {
    return setCell(row, column, content, null);
  }

  @Override
  public Cell setCellFormula(int row, int column, String formula) {
    if (!validateCellIndex(row, column, formula)) {
      // do not try to create an invalid cell
      return null;
    }

    Cell cell = getOrCreateCell(row, column);
    // cell.setCellType(Cell.CELL_TYPE_FORMULA); // deprecated!
    cell.setCellFormula(formula);
    return cell;
  }

  @Override
  public Cell setCell(int row, int column, double value) {
    return setCell(row, column, value, null);
  }

  @Override
  public Cell setCell(int row, int column, double value, CellStyle style) {
    if (!validateCellIndex(row, column, value + "")) {
      return null;
    }
    Cell cell = getOrCreateCell(row, column);
    cell.setCellValue(value);
    if (style != null) {
      cell.setCellStyle(style);
    }
    return cell;
  }
  
  public void setHyperlink(Cell cell, String link) {
    Hyperlink hyperlink = getCreationHelper().createHyperlink(HyperlinkType.URL);
    hyperlink.setAddress(link);
    cell.setHyperlink(hyperlink);
  }

  /* SHEET MANAGEMENT */
  
  public Sheet getSheet(String name) {
    return getWorkbook().getSheet(name);
  }
  
  public Sheet setCurrentSheet(String name) {
    Sheet s = getWorkbook().getSheet(name);
    if(s!=null)
      setCurrentSheet(s);
    return s;
  }

  /** {@inheritDoc} */
  @Override
  public Sheet getCurrentSheet() {
    return sheets.get(getCurrentSheetId());
  }

  /** {@inheritDoc} */
  @Override
  public int getCurrentSheetId() {
    return currentSheetId;
  }

  /** {@inheritDoc} */
  @Override
  public void setCurrentSheetId(int s) {
    this.currentSheetId = s;

    this.drawing = getCurrentSheet().createDrawingPatriarch();
  }

  /** Set current Sheet in which cell are set. Different from {@link setActiveSheet}. */
  public void setCurrentSheet(Sheet s) {
	for(Map.Entry<Integer, Sheet> sheet : sheets.entrySet()) {
		if(sheet.getValue().equals(s)) {
			setCurrentSheetId(sheet.getKey());
			return;
		}
	}
	throw new RuntimeException("sheet not found");
  }
  
  public void setSheetOrder(String name, int order) {
    getWorkbook().setSheetOrder(name, order);
  }
  
  /** Set currently displayed sheet (different from {@link setCurrentSheet}).*/
  public void setActiveSheet(int order) {
    getWorkbook().setActiveSheet(order);
  }

  /** {@inheritDoc} */
  @Override
  public int newSheet(int index, String name) {
    Sheet s = workbook.createSheet(name);
    sheets.put(index, s);
    return index;
  }
  
  //@Override
  public Sheet newSheet(String name) {
    Sheet s = workbook.createSheet(name);
    sheets.put(workbook.getNumberOfSheets(), s);
    return s;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Sheet> getAllSheets() {
    return sheets.values();
  }
  
  public List<String> getAllSheetNames(){
    List<String> names=  new ArrayList<String>();
    for(Sheet s: sheets.values()) {
      names.add(s.getSheetName());
    }
    return names;
  }

  protected boolean sheetInitialized(int index) {
    return sheets.containsKey(index);
  }

  /* FORMATTING SHORTCUTS */

  /** {@inheritDoc} */
  @Override
  public void setRowHeight(int row, int height) {
    getOrCreateRow(row).setHeight((short) height);
  }

  // Set the width (in units of 1/256th of a character width)
  /** {@inheritDoc} */
  @Override
  public void setColumnWidth(int column, int width) {
    getCurrentSheet().setColumnWidth(column, width);
  }

  /** {@inheritDoc} */
  @Override
  public void setColumnWidthAuto(int column) {
    getCurrentSheet().autoSizeColumn(column);
  }

  /** {@inheritDoc} */
  @Override
  public void setFreezePane(int colSplit, int rowSplit) {
    getCurrentSheet().createFreezePane(colSplit, rowSplit);
  }

  /** {@inheritDoc} */
  @Override
  public void setFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
    getCurrentSheet().createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
  }

  /** {@inheritDoc} */
  @Override
  public void setSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow,
      int activePane) {
    getCurrentSheet().createSplitPane(xSplitPos, ySplitPos, leftmostColumn, topRow, activePane);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("deprecation")
  public void mergeRange(int firstRow, int firstColumn, int lastRow, int lastColumn) {
    getCurrentSheet()
        .addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn));
  }

  /* BUILDER METHODS */

  /** Return a new cell style instance for the choosen workbook {@link Type}. */
  public CellStyle newCellStyle() {
    return workbook.createCellStyle();
  }

  public Cell getOrCreateCell(int i, int j) {
    Sheet sheet = getCurrentSheet();
    Row row = sheet.getRow(i);
    if (row == null) {
      row = sheet.createRow(i);
    }
    Cell cell = row.getCell(j);
    if (cell == null) {
      cell = row.createCell(j);
    }
    return cell;
  }
  
  public Cell getCell(int i, int j) {
    Sheet sheet = getCurrentSheet();
    Row row = sheet.getRow(i);
    if (row == null) {
      return null;
    }
    Cell cell = row.getCell(j);
    if (cell == null) {
      return null;
    }
    return cell;
  }

  protected Row getOrCreateRow(int i) {
    Sheet sheet = getCurrentSheet();
    Row row = sheet.getRow(i);
    if (row == null) {
      row = sheet.createRow(i);
    }
    return row;
  }

  /* COMMENTS */

  /** {@inheritDoc} */
  @Override
  public Comment addComment(Cell cell, String text, int row, int col, int colWidth, int rowHeight) {
    Comment comment = buildComment(text, row, col, colWidth, rowHeight);
    if (comment != null)
      cell.setCellComment(comment);
    return comment;
  }

  /**
   * Return a Comment.
   *
   * Comments are supported only on XLS file (HSSF framework).
   *
   * @param row
   * @param col
   * @param colWidth
   * @param rowHeight
   * @return
   */
  public Comment buildComment(String text, int row, int col, int colWidth, int rowHeight) {
    ClientAnchor anchor = create.createClientAnchor();
    anchor.setCol1(col);
    anchor.setCol2(col + colWidth);
    anchor.setRow1(row);
    anchor.setRow2(row + rowHeight);

    // Create the comment and set the text+author
    Comment comment = null;
    if (drawing instanceof HSSFPatriarch) {
      HSSFPatriarch p = (HSSFPatriarch) drawing;
      comment = p.createComment((HSSFAnchor) anchor);
    } else if (drawing instanceof XSSFDrawing) {
      // log.error("comments not supported on XSSFDrawing, i.e. XLSX files");
      XSSFDrawing p = (XSSFDrawing) drawing;
      comment = p.createCellComment(anchor);
    }
    if (comment != null) {
      RichTextString str = create.createRichTextString(text);
      comment.setString(str);
      comment.setAuthor("");

      // Assign the comment to the cell
      return comment;
    } else
      return null;
  }

  /* COLORS */

  /** {@inheritDoc} */
  @Override
  public CellStyle newColoredCellStyle(ByteColor color) {
    CellStyle style = newCellStyle();
    style.setFillForegroundColor(getColor(color).getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }

  @Override
  public CellStyle newColoredCellStyle(IndexedColors color) {
    CellStyle style = newCellStyle();
    style.setFillForegroundColor(color.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
  }

  @Override
  public HSSFColor getColor(ByteColor color) {
    return getColor(color.r, color.g, color.b);
  }

  public HSSFColor getColor(byte r, byte g, byte b) {
    HSSFWorkbook hwb = getHSSFWorkbook();
    HSSFPalette palette = hwb.getCustomPalette();
    HSSFColor color = palette.findSimilarColor(r, g, b);
    return color;
  }

  /* PICTURES */

  public int loadPicture(String image) throws IOException {
    InputStream is = new FileInputStream(image);
    byte[] bytes = IOUtils.toByteArray(is);
    int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
    is.close();
    return pictureIdx;
  }

  public void setPicture(int pictureIdx, int col1, int row1, boolean resize) {
    ClientAnchor anchor = create.createClientAnchor();
    // set top-left corner of the picture,
    // subsequent call of Picture#resize() will operate relative to it
    anchor.setCol1(col1);
    anchor.setRow1(row1);
    Picture pict = drawing.createPicture(anchor, pictureIdx);

    // auto-size picture relative to its top-left corner
    if (resize)
      pict.resize();
  }

  /* FONTS */

  public Font getBoldFont() {
    return boldFont;
  }

  public Font newFont(int size) {
    Font newFont = workbook.createFont();
    // newFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
    newFont.setFontHeightInPoints((short) size);
    return newFont;
  }

  /* IO */

  /** {@inheritDoc} */
  @Override
  public void save(String file) throws IOException {
    save(new File(file));
  }

  /** {@inheritDoc} */
  @Override
  public void save(File file) throws IOException {
    FileOutputStream fileOut = new FileOutputStream(file);
    workbook.write(fileOut);
    fileOut.close();
  }

  public static Workbook load(String file) throws IOException {
    return load(new File(file));
  }

  public static Workbook load(File file) throws IOException {
    InputStream inp = new FileInputStream(file);
    return WorkbookFactory.create(inp);
  }

  public CellStyles loadStyleSheet(String sheetName) {
    CellStyles style = new CellStyles();
    style.load(getSheet(sheetName));
    return style;
  }
  
  public CreationHelper getCreationHelper() {
    return create;
  }
}
