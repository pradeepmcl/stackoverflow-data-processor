package edu.rit.se.pkm.so.xmlimporter.posts;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PostHandler extends DefaultHandler {

  private int mCounter = 0;
  private final PreparedStatement mStmt;
  private final Map<String, Integer> mPostColumns;
  private final SimpleDateFormat soDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");

  public PostHandler(PreparedStatement stmt, Map<String, Integer> postColumns) {
    super();
    mStmt = stmt;
    mPostColumns = postColumns;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equals("row")) {
      try {
        int i = 0;
        for (String postColumnName : mPostColumns.keySet()) {
          int postColumnType = mPostColumns.get(postColumnName);
          
          if (attributes.getValue(postColumnName) == null) {
            mStmt.setNull(++i, postColumnType);
          } else {
            if (postColumnType == java.sql.Types.INTEGER) {
              mStmt.setLong(++i, Long.parseLong(attributes.getValue(postColumnName)));
            } else if (postColumnType == java.sql.Types.TINYINT) {
              mStmt.setInt(++i, Integer.parseInt(attributes.getValue(postColumnName)));
            } else if (postColumnType == java.sql.Types.VARCHAR) {
              mStmt.setString(++i, attributes.getValue(postColumnName));
            } else if (postColumnType == java.sql.Types.TIMESTAMP) {
              Date parsedDate = soDateFormat.parse(attributes.getValue(postColumnName));
              Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
              mStmt.setTimestamp(++i, timestamp);
            } else {
              throw new IllegalArgumentException(
                  "Uknown type: " + postColumnName + ", " + postColumnType);
            }
          }
        }
        mStmt.addBatch();
      } catch (NumberFormatException | SQLException | ParseException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) {
    mCounter++;
    if (mCounter % 10000 == 0) {
      try {
        mStmt.executeBatch();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void endDocument() {
    try {
      mStmt.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
