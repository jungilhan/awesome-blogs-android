# Usage Examples

The following are some basic usage examples.

## Simplest Usage

For straight-up Markdown, simply use the `Remark` class directly, like so:

    import com.overzealous.remark.Remark;
    
    Remark remark = new Remark();
    String htmlInput = ...;
    String markdown = remark.convertFragment(htmlImput);

## Alternate Usage

Because Remark uses [jsoup][] under the surface, it can parse HTML from a variety of sources, including:

*   Whole HTML Files

        remark.convert(new File("path/to/file.html"));
        
*   Download HTML files from a URL

        remark.convert(new URL("http://www.overzealous.com/"), 15000);

*   Convert whole HTML files already loaded in-memory

        remark.convert(htmlInput);

*   Convert fragments with a base URI to allow for relative links

        remark.convertFragment(htmlInput, "http://www.example.com");
        
    **Note:** Relative links will be *removed* from the final document if you do not provide a base URI on fragments!


## Compatibility With Markdown Extensions

For some of the more common Markdown extensions, you can use the `Options` class to create a `Remark` converter with additional functionality.

    import com.overzealous.remark.Remark;
    import com.overzealous.remark.Options;
    
    // PHP Markdown Extra
    Remark markdownExtraRemark = new Remark(Options.markdownExtra());
    
    // MultiMarkdown
    Remark multiMarkdownRemark = new Remark(Options.multiMarkdown());
    
    // Github Flavored Markdown
    Remark githubMarkdown = new Remark(Options.github());
    
    // Pegdown with all extensions enabled
    Remark pegdownMarkdown = new Remark(Options.pegdownAllExtensions());

For more information on what these static option sets provide, please check out [the JavaDoc API][javadoc Options].

## Custom Options

You can also set up your own options, to change the behavior for certain features.  The following is just a highlight.  The [JavaDoc API for `Options`][javadoc Options] has the complete list of settings.

> Note: all options must be configured **before** creating the Remark object.  However, the Options object is cloned when creating the Remark instance, so you can reuse the Options object to create different Remark instances.

### Replace Tables with plain text 

Even if your markdown parser doesn't support tables, **Remark** can be used to convert tables into a simplified plain-text representation.

    Options opts = Options.markdown();
    opts.tables = Options.Tables.CONVERT_TO_CODEBLOCK;

If you want them removed completely, for (security reasons, for example), you can change the option like this:

    opts.tables = Options.Tables.REMOVE;

### Use Simple Link IDs

By default, Remark attempts to use the description of a link to generate link ids.  (If you want links inline, see the next section.)

This means that this input:

    <p><a href="http://example.com">An Example</a></p>
    <p><a href="http://google.com">Google Me</a></p>
    
gets converted into something like:

    [An Example][]
    [Google Me][]
    
    [An Example]: http://example.com
    [Google Me]: http://google.com

If you'd prefer the resulting link IDs to be simple numeric IDs, change the setting like so:

    Options opts = Options.markdown();
    opts.simpleLinkIds = true;
    
Now the output will be:

    [An Example][1]
    [Google Me][2]
    
    [1]: http://example.com
    [2]: http://google.com

> Note: this setting affects image links as well.

### Inline Links

You can also change the output so that all links are inline, which may be easier for your users.

    Options opts = Options.markdown();
    opts.inlineLinks = true;

> Note: this setting affects image links as well.


### Preserve Relative Links

By default, relative links in JSoup are resolved against your base URI (whether explicitly provided or determined automatically when downloading a file). If you'd prefer to keep relative links, you can set the `preserveRelativeLinks` option to `true`.

    Options opts = Options.markdown();
    opts.preserveRelativeLinks = true;

### Allowing custom HTML tags

If you decide you want to allow custom HTML tags, these (and their attributes) can be added to the Options file before creating
the Remark object.

    import com.overzealous.remark.Remark;
    import com.overzealous.remark.Options;
    import com.overzealous.remark.IgnoredHtmlElement;
    
    Options opts = Options.markdown();
    opts.ignoredHtmlElements.add(IgnoredHtmlElement.create("quote", "class");
    Remark remarkWithQuote = new Remark(opts);

These elements (and the specified attributes) will be kept in the final output.  It is up to you to secure these items.

> Note: this will not allow you to override default bahavior. For example, there is no way to preserve `<span>` tags, since they are analyzed for bold and italic font changes.
>
> This means that, currently, there is no way to support color within the Markdown output.

## Streaming the Output

Usually, the Remark conversion happens completely in memory.  If you are converting a large document, however, you can stream the conversion process.  This helps a tiny bit.  The resulting document can be streamed to any `Writer` or `OutputStream`.

    Writer myWriter = null;
    try {
    	// look up your writer
    	myWriter = ...;
    	Remark remark = new Remark().withWriter(myWriter);
    	remark.convert(new URL("http://www.google.com"), 15000);
    } finally {
    	myWriter.close();
    }



[jsoup]: http://jsoup.org/
[javadoc Options]: ../javadoc/com/overzealous/remark/Options.html