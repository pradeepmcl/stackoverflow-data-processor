package edu.rit.se.pkm.so.xmlimporter.posts;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.XMLConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PostXmlManager {

  private static final Properties props = new Properties();

  public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException,
      ParserConfigurationException, SAXException {

    Map<String, Integer> postColumns = populatePostColumnsMap();
    String postsInsertQuery = buildPostsInsertQuery(postColumns);

    try (InputStream inStream = PostHandler.class.getResourceAsStream("/application.properties")) {
      props.load(inStream);
      Class.forName(props.getProperty("jdbc.driverClassName"));

      try (
          Connection mConn = DriverManager.getConnection(
              props.getProperty("jdbc.url") + "?user=" + props.getProperty("jdbc.username")
                  + "&password=" + props.getProperty("jdbc.password"));
          PreparedStatement mStmt = mConn.prepareStatement(postsInsertQuery)) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Without this line we get an exception around a million rows
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

        SAXParser parser = factory.newSAXParser();
        File postsXmlFile = new File(props.getProperty("xml.filename.posts"));
        PostHandler postsHandler = new PostHandler(mStmt, postColumns);

        parser.parse(postsXmlFile, postsHandler);
      }
    }
  }

  private static Map<String, Integer> populatePostColumnsMap() {
    Map<String, Integer> postColumns = new LinkedHashMap<String, Integer>();
    postColumns.put("Id", java.sql.Types.INTEGER);
    postColumns.put("PostTypeId", java.sql.Types.TINYINT);
    postColumns.put("ParentId", java.sql.Types.INTEGER);
    postColumns.put("AcceptedAnswerId", java.sql.Types.INTEGER);

    postColumns.put("OwnerUserId", java.sql.Types.INTEGER);
    postColumns.put("LastEditorUserId", java.sql.Types.INTEGER);
    postColumns.put("OwnerDisplayName", java.sql.Types.VARCHAR);
    postColumns.put("LastEditorDisplayName", java.sql.Types.VARCHAR);
    postColumns.put("Title", java.sql.Types.VARCHAR);
    postColumns.put("Tags", java.sql.Types.VARCHAR);
    postColumns.put("Body", java.sql.Types.VARCHAR);
    postColumns.put("Score", java.sql.Types.INTEGER);
    postColumns.put("ViewCount", java.sql.Types.INTEGER);
    postColumns.put("FavoriteCount", java.sql.Types.INTEGER);
    postColumns.put("AnswerCount", java.sql.Types.INTEGER);
    postColumns.put("CommentCount", java.sql.Types.INTEGER);
    postColumns.put("CreationDate", java.sql.Types.TIMESTAMP);
    postColumns.put("CommunityOwnedDate", java.sql.Types.TIMESTAMP);
    postColumns.put("LastEditDate", java.sql.Types.TIMESTAMP);
    postColumns.put("ClosedDate", java.sql.Types.TIMESTAMP);
    postColumns.put("LastActivityDate", java.sql.Types.TIMESTAMP);

    return postColumns;
  }

  private static String buildPostsInsertQuery(Map<String, Integer> postColumns) {
    StringBuilder postInsertQueryBuilder = new StringBuilder("insert into Posts (");
    for (String postsColName : postColumns.keySet()) {
      postInsertQueryBuilder.append(postsColName + ",");
    }

    postInsertQueryBuilder.replace(postInsertQueryBuilder.length() - 1,
        postInsertQueryBuilder.length(), ") values (");

    for (int i = 0; i < postColumns.size(); i++) {
      postInsertQueryBuilder.append("?,");
    }

    postInsertQueryBuilder.replace(postInsertQueryBuilder.length() - 1,
        postInsertQueryBuilder.length(), ")");

    return postInsertQueryBuilder.toString();
  }
}
