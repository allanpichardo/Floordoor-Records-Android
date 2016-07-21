package com.mylovemhz.floordoorrecords.net;

import android.net.Uri;
import android.text.Html;
import android.util.Log;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Enclosure;
import com.pkmmte.pkrss.parser.Parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class FdRss2Parser extends Parser {

    private final List<Article> articleList = new ArrayList<Article>();
    private final DateFormat dateFormat;
    private final Pattern pattern;
    private final XmlPullParser xmlParser;

    public static final String TAG = "FD Parser";

    public FdRss2Parser(){
        // Initialize DateFormat object with the default date formatting
        dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
        pattern = Pattern.compile("-\\d{1,4}x\\d{1,4}");

        // Initialize XmlPullParser object with a common configuration
        XmlPullParser parser = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            parser = factory.newPullParser();
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        xmlParser = parser;
    }

    @Override
    public List<Article> parse(String rssStream) {
        // Clear previous list and start timing execution time
        articleList.clear();
        long time = System.currentTimeMillis();

        try {
            // Get InputStream from String and set it to our XmlPullParser
            InputStream input = new ByteArrayInputStream(rssStream.getBytes());
            xmlParser.setInput(input, null);

            // Reuse Article object and event holder
            Article article = new Article();
            int eventType = xmlParser.getEventType();

            // Loop through the entire xml feed
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = xmlParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) // Start a new instance
                            article = new Article();
                            // Enclosures not readable as text by XmlPullParser in Android and will fail in handleNode, considered not a bug
                            // https://code.google.com/p/android/issues/detail?id=18658
                        else if (tagname.equalsIgnoreCase("enclosure")) {
                            article.setEnclosure(new Enclosure(xmlParser));
                        }
                        else // Handle this node if not an entry tag
                            handleNode(tagname, article);
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // Generate ID
                            article.setId(Math.abs(article.hashCode()));

                            // (Optional) Log a minimized version of the toString() output
                            log(TAG, article.toShortString(), Log.INFO);

                            // Add article object to list
                            articleList.add(article);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlParser.next();
            }
        }
        catch (IOException e) {
            // Uh oh
            e.printStackTrace();
        }
        catch (XmlPullParserException e) {
            // Oh noes
            e.printStackTrace();
        }

        // Output execution time and return list of newly parsed articles
        log(TAG, "Parsing took " + (System.currentTimeMillis() - time) + "ms");
        return articleList;
    }

    /**
     * Handles a node from the tag node and assigns it to the correct article value.
     * @param tag The tag which to handle.
     * @param article Article object to assign the node value to.
     * @return True if a proper tag was given or handled. False if improper tag was given or
     * if an exception if triggered.
     */
    private boolean handleNode(String tag, Article article) {

        try {
            if(xmlParser.next() != XmlPullParser.TEXT)
                return false;

            if (tag.equalsIgnoreCase("link"))
                article.setSource(Uri.parse(xmlParser.getText()));
            else if (tag.equalsIgnoreCase("title"))
                article.setTitle(xmlParser.getText());
            else if (tag.equalsIgnoreCase("description")) {
                String encoded = xmlParser.getText();
                //article.setDescription(Html.fromHtml(encoded.replaceAll("<img.+?>", "")).toString());
                article.setDescription(Html.fromHtml(encoded).toString());
            }
            else if (tag.equalsIgnoreCase("content:encoded")) {
                String s = xmlParser.getText();
                article.setContent(s);
            }
            else if (tag.equalsIgnoreCase("wfw:commentRss"))
                article.setComments(xmlParser.getText());
            else if (tag.equalsIgnoreCase("category"))
                article.setNewTag(xmlParser.getText());
            else if (tag.equalsIgnoreCase("dc:creator"))
                article.setAuthor(xmlParser.getText());
            else if (tag.equalsIgnoreCase("pubDate")) {
                article.setDate(getParsedDate(xmlParser.getText()));
            }
            else if (tag.equalsIgnoreCase("featuredImage")){
                article.setImage(Uri.parse(xmlParser.getText()));
            }

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts a date in the "EEE, d MMM yyyy HH:mm:ss Z" format to a long value.
     * @param encodedDate The encoded date which to convert.
     * @return A long value for the passed date String or 0 if improperly parsed.
     */
    private long getParsedDate(String encodedDate) {
        try {
            return dateFormat.parse(dateFormat.format(dateFormat.parseObject(encodedDate))).getTime();
        }
        catch (ParseException e) {
            log("FD RSS Parser", "Error parsing date " + encodedDate, Log.WARN);
            e.printStackTrace();
            return 0;
        }
    }

}
