package eu.fiskur.markdownview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MarkdownView extends RelativeLayout {
  private static final String TAG = MarkdownView.class.getSimpleName();
  private WebView webView;
  private WebSettings webSettings;
  private MarkdownWebViewClient webViewClient;
  private OnProgressChangedListener onProgressChangedListener;

  public interface OnProgressChangedListener {

    void onChanged(int progress);
  }

  private static final String MARKDOWN_MARKUP_TEMPLATE = "<!doctype html>\n"
      + "<html>\n"
      + "<head>\n"
      + "  <meta charset=\"utf-8\"/>\n"
      + "  <title>Marked in the browser</title>\n"
      + "  <link rel=\"stylesheet\" href=\"./githubmarkdown.css\">\n"
      + "  <script src=\"./marked.js\"></script>\n"
      + "  <script src=\"./markdown.js\"></script>\n"
      + "</head>\n"
      + "<body>\n"
      + "  <div id=\"content\" class=\"markdown-body\"></div>\n"
      + "  <div class=\"footer\">"
      + "    <h4 class=\"visit\"><a href='%s'>페이지 방문하기</a></h4>"
      + "  </div>\n"
      + "  <script>\n"
      + "    document.getElementById('content').innerHTML =\n"
      + "      marked('%s');\n"
      + "  </script>"
      + "</body>\n"
      + "</html>";

  public MarkdownView(Context context) {
    super(context);
    setup();
  }

  public MarkdownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup();
    readAttributes(context, attrs);
  }

  public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup();
    readAttributes(context, attrs);
  }

  private void readAttributes(Context context, AttributeSet attrs){
    TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MarkdownView, 0, 0);
    int rawFileId;
    int stringId;
    try {
      rawFileId = typedArray.getResourceId(R.styleable.MarkdownView_rawFile, -1);
      stringId = typedArray.getResourceId(R.styleable.MarkdownView_markdown, -1);
    }finally{
      typedArray.recycle();
    }

    if(rawFileId != -1 && stringId != -1){
      Log.e(TAG, "Cannot set both raw file and string resource xml attributes");
      return;
    }

    if(rawFileId != -1){
      showMarkdown(rawFileId);
    }

    if(stringId != -1){
      showMarkdown(getResources().getString(stringId));
    }
  }

  private void setup(){
    LayoutInflater.from(getContext()).inflate(R.layout.markdown_view, this);
    webView = (WebView) findViewById(R.id.markdown_web_view);

    webSettings = webView.getSettings();
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setJavaScriptEnabled(true);

    //To handle web links:
    webViewClient = new MarkdownWebViewClient(getContext());
    webView.setWebViewClient(webViewClient);
    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        if (onProgressChangedListener != null) {
          onProgressChangedListener.onChanged(newProgress);
        }
      }
    });
    allowGestures(false);
  }

  public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
    this.onProgressChangedListener = onProgressChangedListener;
  }

  public void setOnLoadingCompleteAction(Runnable completeAction) {
    webViewClient.setOnLoadingCompleteAction(completeAction);
  }

  public void setOnOverrideUrlAction(MarkdownWebViewClient.OnOverrideUrlListener onOverrideUrlListener, Runnable fallbackAction) {
    webViewClient.setOnOverrideUrlAction(onOverrideUrlListener, fallbackAction);
  }

  public void showMarkdown(String markdown){
    showMarkdown("#", markdown);
  }

  public void showMarkdown(String link, String markdown){
    if(markdown == null || markdown.isEmpty()){
      return;
    }
    markdown = markdown.replace("\n", "\\n");
    webView.loadDataWithBaseURL("file:///android_asset/", String.format(MARKDOWN_MARKUP_TEMPLATE, link, markdown), "text/html", "utf-8", null);
  }

  public void showMarkdown(int rawFileId){
    StringBuffer sb = new StringBuffer();
    BufferedReader reader = null;
    InputStreamReader isr = null;
    try {
      isr = new InputStreamReader(getContext().getResources().openRawResource(rawFileId));
      reader = new BufferedReader(isr);

      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
      Log.e(TAG, e.toString());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {}
      }

      if(isr != null){
        try {
          isr.close();
        } catch (IOException e) {}
      }
    }
    showMarkdown(sb.toString());
  }

  //Off by default
  public void allowGestures(boolean allowGestures){
    if(allowGestures){
      webSettings.setBuiltInZoomControls(true);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        webSettings.setDisplayZoomControls(false);
      }
    }else{
      webSettings.setBuiltInZoomControls(false);
    }
  }

  private static class MarkdownWebViewClient extends WebViewClient {

    private final Context context;
    private Runnable completeAction;
    private OnOverrideUrlListener onOverrideUrlListener;
    private Runnable fallbackAction;

    public interface OnOverrideUrlListener {

      void onUrl(String url);
    }

    MarkdownWebViewClient(Context context) {
      this.context = context;
    }

    void setOnLoadingCompleteAction(Runnable completeAction) {
      this.completeAction = completeAction;
    }

    void setOnOverrideUrlAction(OnOverrideUrlListener onOverrideUrlListener, Runnable fallbackAction) {
      this.onOverrideUrlListener = onOverrideUrlListener;
      this.fallbackAction = fallbackAction;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      if (completeAction != null) {
        completeAction.run();
      }
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (onOverrideUrlListener == null) {
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
      } else {
        try {
          onOverrideUrlListener.onUrl(url);
        } catch (ActivityNotFoundException e) {
          fallbackAction.run();
        }
      }
      return true;
    }
  }
}
