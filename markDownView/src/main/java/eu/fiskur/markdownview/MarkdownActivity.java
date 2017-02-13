package eu.fiskur.markdownview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MarkdownActivity extends AppCompatActivity {
  private static final String TAG = "MarkdownActivity";
  public static final String EXTRA_SHOW_TOOLBAR = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_SHOW_TOOLBAR";
  public static final String EXTRA_ALLOW_GESTURES = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_ALLOW_GESTURES";
  public static final String EXTRA_TOOLBAR_TITLE = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_TOOLBAR_TITLE";
  public static final String EXTRA_MARKDOWN = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_MARKDOWN";
  public static final String EXTRA_STRING_RESOURCE = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_STRING_RESOURCE";
  public static final String EXTRA_RAW_FILE = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_RAW_FILE";
  public static final String EXTRA_DISPLAY_HOMEASUP = "eu.fiskur.markdownview.MarkdownActivity.EXTRA_DISPLAY_HOMEASUP";

  private MarkdownView markdownView;

  public static class IntentBuilder {
    private Bundle extras;

    public static IntentBuilder getBuilder() {
      IntentBuilder builder = new IntentBuilder();
      return builder;
    }

    private IntentBuilder() {
      extras = new Bundle();
    }

    public IntentBuilder showToolbar(boolean showToolbar) {
      extras.putBoolean(EXTRA_SHOW_TOOLBAR, showToolbar);
      return this;
    }

    public IntentBuilder displayHomeAsUp(boolean displayHomeAsUp) {
      extras.putBoolean(EXTRA_DISPLAY_HOMEASUP, displayHomeAsUp);
      return this;
    }

    public IntentBuilder title(String title) {
      extras.putString(EXTRA_TOOLBAR_TITLE, title);
      return this;
    }

    public IntentBuilder allowGestures(boolean allowGestures) {
      extras.putBoolean(EXTRA_ALLOW_GESTURES, allowGestures);
      return this;
    }

    public IntentBuilder markdown(String markdown) {
      extras.putString(EXTRA_MARKDOWN, markdown);
      return this;
    }

    public IntentBuilder resourceId(int stringResourceId) {
      extras.putInt(EXTRA_STRING_RESOURCE, stringResourceId);
      return this;
    }

    public IntentBuilder rawFileId(int rawFileId) {
      extras.putInt(EXTRA_RAW_FILE, rawFileId);
      return this;
    }

    public Intent build(Context context) {
      Intent intent = new Intent(context, MarkdownActivity.class);
      intent.putExtras(extras);
      return intent;
    }
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_markdown);

    markdownView = (MarkdownView) findViewById(R.id.markdown_view);

    setup();
  }

  private void setup(){
    Log.d(TAG, "setup()");
    Bundle bundle = getIntent().getExtras();
    boolean showToolbar = bundle.getBoolean(EXTRA_SHOW_TOOLBAR);
    String toolbarTitle = bundle.getString(EXTRA_TOOLBAR_TITLE);
    if(showToolbar || toolbarTitle != null){
      getSupportActionBar().setTitle(toolbarTitle);

      boolean displayHomeAsUp = bundle.getBoolean(EXTRA_DISPLAY_HOMEASUP);
      if(displayHomeAsUp){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }
    }else{
      getSupportActionBar().hide();
    }

    boolean allowGestures = bundle.getBoolean(EXTRA_ALLOW_GESTURES);
    if(!allowGestures){
      markdownView.allowGestures(false);
    }

    String markdown = bundle.getString(EXTRA_MARKDOWN);
    int stringId = bundle.getInt(EXTRA_STRING_RESOURCE, -1);
    int rawId = bundle.getInt(EXTRA_RAW_FILE, -1);

    if(markdown != null && !markdown.isEmpty() && stringId != -1){
      Log.e(TAG, "Cannot set both a markdown String literal and a String resource Id");
    }else if(markdown != null && !markdown.isEmpty() && rawId != -1){
      Log.e(TAG, "Cannot set both a markdown String literal and a Raw file Id");
    }else if(rawId != -1 && stringId != -1){
      Log.e(TAG, "Cannot set both a Raw file Id and a String resource Id");
    }else{
      if(markdown != null && !markdown.isEmpty()){
        markdownView.showMarkdown(markdown);
      }
      if(stringId != -1){
        markdownView.showMarkdown(getResources().getString(stringId));
      }
      if(rawId != -1){
        markdownView.showMarkdown(rawId);
      }
    }

    Log.d(TAG, "markdown: " + markdown);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    finish();
    return super.onOptionsItemSelected(item);
  }
}
