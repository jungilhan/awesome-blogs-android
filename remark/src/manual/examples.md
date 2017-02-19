# Examples

Here are some basic input/output examples.

## Options: None (Straight Markdown)

### Hello World

 *  HTML

        <p>Hello World</p>

 *  Markdown

        Hello World


### Lists & Styling

 *  HTML

		<em>Unordered</em>
		<ul>
			<li>Item 1</li>
			<li>Item 2</li>
			<li>Item 3</li>
		</ul>
		<strong>Ordered</strong>
		<ol>
			<li>Item 1</li>
			<li>Item 2</li>
			<li>Item 3</li>
		</ol>

 *  Markdown
    
		*Unordered*
		
		 *  Item 1
		 *  Item 2
		 *  Item 3
		
		**Ordered**
		
		1.  Item 1
		2.  Item 2
		3.  Item 3


### Links

 *   HTML
 
        <ol>
			<li><a href="http://www.example.com">Example.com</a></li>
			<li><a href="http://www.google.com">Google</a></li>
			<li><a href="http://www.yahoo.com">Yahoo!</a></li>
			<li><a href="http://www.example.com">Another Example.com</a></li>
		</ol>
		
 *   Markdown
 
		1.  [Example.com][]
		2.  [Google][]
		3.  [Yahoo!][Yahoo]
		4.  [Another Example.com][Example.com]
		
		
		[Example.com]: http://www.example.com
		[Google]: http://www.google.com
		[Yahoo]: http://www.yahoo.com


### Blockquotes and Code Samples

 *  HTML
 
		<blockquote>
			Yes, Me Too
			<blockquote>
				I agree
				<blockquote>
					Top posting is confusing
				</blockquote>
			</blockquote>
		</blockquote>
		
		<pre>// Ain't Groovy Grand?
		// From: http://marxsoftware.blogspot.com/2011/06/ten-groovy-one-liners-to-impress-your.html
		(1..100).each{println "${it%3?'':'Fizz'}${it%5?'':'Buzz'}" ?: it }
		</pre>

 *  Markdown
 
		> Yes, Me Too
		> 
		> > I agree
		> > 
		> > > Top posting is confusing
		
			// Ain't Groovy Grand?
			// From: http://marxsoftware.blogspot.com/2011/06/ten-groovy-one-liners-to-impress-your.html
			(1..100).each{println "${it%3?'':'Fizz'}${it%5?'':'Buzz'}" ?: it }


### Broken and Poor HTML

In this example, you can see how Remark (along with JSoup) does it's best to salvage a usable result from bad input. 

 *  HTML

		<p>
			<font style="font-style: italic;">
			<span style="font-weight: bold">
			   <b><i>This is really bad HTML</span></b></i>
		
		<div>
			<div>
			   <p>I'm deeply nested</p>
			</div>
			<div>I'm not in a paragraph tag at all!
		</div>
		<var>I'm a useless tag!!</var>

 *  Markdown

		***This is really bad HTML***
		
		I'm deeply nested
		
		I'm not in a paragraph tag at all!
		
		I'm a useless tag!!

## Options: Markdown Extra

These are some unique results when the options are set to use PHP Markdown Extra special features.

### Header IDs & Abbreviations

 *  HTML

		<h1 id="header1">Header 1</h1>
		<p>This is <abbr title="Hyper-Text Markup Language">HTML</abbr>!</p>
		
 *  Markdown

		# Header 1 #    {#header1}
		
		This is HTML!
		
		
		*[HTML]: Hyper-Text Markup Language


### Definition Lists

 *  HTML

		<dl>
			<dt>HTML</dt>
			<dd>A markup language commonly used on the web.</dd>
			<dt>Markdown</dt>
			<dd>A markup language specifically designed to be human-readable.</dd>
		</dl>

 * Markdown
 
		HTML
		:   A markup language commonly used on the web.
		
		Markdown
		:   A markup language specifically designed to be human-readable.


### Markdown Extra Tables

 *   HTML

		<table>
			<thead>
				<tr>
					<th>&nbsp;</th>
					<th colspan="2">Grouping</th>
				</tr>
				<tr>
					<th>First Header</th>
					<th>Second Header</th>
					<th>Third Header</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Content</td>
					<td colspan="2" align="center"><em>Long Cell</em></td>
				</tr>
				<tr>
					<td>Content</td>
					<td align="center"><strong>Cell</strong></td>
					<td align="right">Cell</td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<td>New Section</td>
					<td align="center">More</td>
					<td align="right">Data</td>
				</tr>
				<tr>
					<td>And more</td>
					<td colspan="2" align="center">And more</td>
				</tr>
			</tbody>
		</table>

 *  Markdown

		|              |   Grouping    |              |
		| First Header | Second Header | Third Header |
		|:------------ |:-------------:| ------------:|
		| Content      |  *Long Cell*  |              |
		| Content      |   **Cell**    |         Cell |
		| New Section  |     More      |         Data |
		| And more     |   And more    |              |
		
		

## Options: Pegdown (All Extensions)

These are some unique results when the options are set to use Pegdown (All Extensions) special features.

### Autolinks & Entity / Smartquote Reversal

 *  HTML

		<p>This link will render completely inline: <a href="http://www.example.com">http://www.example.com</a></p>
		<p>These fancy characters will be reverted to simple UTF-8 or simple quotes:</p>
		<p>&ldquo;This &mdash; that&hellip;&rdquo;</p>

 *  Markdown

		This link will render completely inline: http://www.example.com
		
		These fancy characters will be reverted to simple UTF-8 or simple quotes:
		
		"This --- that..."


### Tables (with colspan)

Notice that, unlike the previous table example, the column-spanning is preserved.

 *   HTML

		<table>
			<thead>
				<tr>
					<th>&nbsp;</th>
					<th colspan="2">Grouping</th>
				</tr>
				<tr>
					<th>First Header</th>
					<th>Second Header</th>
					<th>Third Header</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Content</td>
					<td colspan="2" align="center"><em>Long Cell</em></td>
				</tr>
				<tr>
					<td>Content</td>
					<td align="center"><strong>Cell</strong></td>
					<td align="right">Cell</td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<td>New Section</td>
					<td align="center">More</td>
					<td align="right">Data</td>
				</tr>
				<tr>
					<td>And more</td>
					<td colspan="2" align="center">And more</td>
				</tr>
			</tbody>
		</table>

 *  Markdown

		|              |          Grouping           ||
		| First Header | Second Header | Third Header |
		|:------------ |:-------------:| ------------:|
		| Content      |         *Long Cell*         ||
		| Content      |   **Cell**    |         Cell |
		| New Section  |     More      |         Data |
		| And more     |          And more           ||
