@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.markdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.redgrapefruit.openmodinstaller.util.BitmapFromImageURL
import org.commonmark.node.*
import org.commonmark.node.Paragraph

/**
 * These functions will render a tree of Markdown nodes parsed with CommonMark.
 * Images will be rendered using Chris Banes Accompanist library (which uses Coil)
 *
 * To use this, you need the following two dependencies:
 * implementation "com.atlassian.commonmark:commonmark:0.15.2"
 *
 * The following is an example of how to use this:
 * ```kotlin
 * val parser = Parser.builder().build()
 * val root = parser.parse(MIXED_MD) as Document
 * val markdownComposer = MarkdownComposer()
 *
 * MarkdownComposerTheme {
 *    MDDocument(root)
 * }
 * ```
 */
private const val TAG_URL = "url"
private const val TAG_IMAGE_URL = "imageUrl"


@Composable
fun MDBlockChildren(parent: Node) {
    var child = parent.firstChild
    var padding = 20

    while (child != null) {
        when (child) {
            is BlockQuote -> MDBlockQuote(child, padding)
            is ThematicBreak -> MDThematicBreak(child, padding)
            is Heading -> MDHeading(child, padding)
            is Paragraph -> MDParagraph(child, padding)
            is FencedCodeBlock -> MDFencedCodeBlock(child, padding)
            is IndentedCodeBlock -> MDIndentedCodeBlock(child, padding)
            is Image -> MDImage(child, padding)
            is BulletList -> MDBulletList(child, padding)
            is OrderedList -> MDOrderedList(child, padding)
        }
        child = child.next
        padding += 20
    }
}

@Composable
fun MDDocument(document: Document) {
    MDBlockChildren(document)
}

@Composable
fun MDHeading(heading: Heading, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.h1
        2 -> MaterialTheme.typography.h2
        3 -> MaterialTheme.typography.h3
        4 -> MaterialTheme.typography.h4
        5 -> MaterialTheme.typography.h5
        6 -> MaterialTheme.typography.h6
        else -> {
            // Invalid header...
            MDBlockChildren(heading)
            return
        }
    }

    val padding = if (heading.parent is Document) 8.dp else 0.dp
    Box(modifier = modifier.padding(bottom = padding)) {
        val text = buildAnnotatedString {
            appendMarkdownChildren(heading, MaterialTheme.colors)
        }
        MarkdownText(text, style, padding.value.toInt())
    }
}

@Composable
fun MDParagraph(paragraph: Paragraph, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (20 + padding).dp)) {
    if (paragraph.firstChild is Image && paragraph.firstChild == paragraph.lastChild) {
        // Paragraph with single image
        MDImage(paragraph.firstChild as Image, padding, modifier)
    } else {
        val pad = if (paragraph.parent is Document) 8.dp else 0.dp
        Box(modifier = modifier.padding(0.dp, pad)) {
            val styledText = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.body1.toSpanStyle())
                appendMarkdownChildren(paragraph, MaterialTheme.colors)
                pop()
            }
            MarkdownText(styledText, MaterialTheme.typography.body1, padding)
        }
    }
}

@Composable
fun MDImage(image: Image, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            bitmap = BitmapFromImageURL(image.destination),
            contentDescription = null,
            modifier = modifier
        )
    }
}

@Composable
fun MDBulletList(bulletList: BulletList, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    val marker = bulletList.bulletMarker
    MDListItems(bulletList, padding, modifier = modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("$marker ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, padding, modifier)
    }
}

@Composable
fun MDOrderedList(orderedList: OrderedList, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    MDListItems(orderedList, padding, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, padding, modifier)
    }
}

@Composable
fun MDListItems(
    listBlock: ListBlock,
    padding: Int,
    modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp),
    item: @Composable (node: Node) -> Unit
) {
    val bottom = if (listBlock.parent is Document) 8.dp else 0.dp
    val start = if (listBlock.parent is Document) 0.dp else 8.dp
    Box(modifier = modifier.padding(bottom = bottom, start = start)) {
        var listItem = listBlock.firstChild
        while (listItem != null) {
            var child = listItem.firstChild
            while (child != null) {
                when (child) {
                    is BulletList -> MDBulletList(child, padding, modifier)
                    is OrderedList -> MDOrderedList(child, padding, modifier)
                    else -> item(child)
                }
                child = child.next
            }
            listItem = listItem.next
        }
    }
}

@Composable
fun MDBlockQuote(blockQuote: BlockQuote, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    val color = MaterialTheme.colors.onBackground
    Box(modifier = modifier.drawBehind {
        drawLine(
            color = color,
            strokeWidth = 2f,
            start = Offset(12.dp.value, 0f),
            end = Offset(12.dp.value, size.height)
        )
    }.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
        val text = buildAnnotatedString {
            pushStyle(
                MaterialTheme.typography.body1.toSpanStyle()
                    .plus(SpanStyle(fontStyle = FontStyle.Italic))
            )
            appendMarkdownChildren(blockQuote, MaterialTheme.colors)
            pop()
        }
        Text(text, modifier)
    }
}

@Composable
fun MDFencedCodeBlock(fencedCodeBlock: FencedCodeBlock, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    val padding = if (fencedCodeBlock.parent is Document) 8.dp else 0.dp
    Box(modifier = modifier.padding(bottom = padding, start = 8.dp)) {
        Text(
            text = fencedCodeBlock.literal,
            style = TextStyle(fontFamily = FontFamily.Monospace),
            modifier = modifier
        )
    }
}

@Composable
fun MDIndentedCodeBlock(indentedCodeBlock: IndentedCodeBlock, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    // Ignored
}

@Composable
fun MDThematicBreak(thematicBreak: ThematicBreak, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    //Ignored
}

fun AnnotatedString.Builder.appendMarkdownChildren(
    parent: Node, colors: Colors
) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Paragraph -> appendMarkdownChildren(child, colors)
            is Text -> append(child.literal)
            is Image -> appendInlineContent(TAG_IMAGE_URL, child.destination)
            is Emphasis -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is StrongEmphasis -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is Code -> {
                pushStyle(TextStyle(fontFamily = FontFamily.Monospace).toSpanStyle())
                append(child.literal)
                pop()
            }
            is HardLineBreak -> {
                append("\n")
            }
            is Link -> {
                val underline = SpanStyle(colors.primary, textDecoration = TextDecoration.Underline)
                pushStyle(underline)
                pushStringAnnotation(TAG_URL, child.destination)
                appendMarkdownChildren(child, colors)
                pop()
                pop()
            }
        }
        child = child.next
    }
}

@Composable
fun MarkdownText(text: AnnotatedString, style: TextStyle, padding: Int, modifier: Modifier = Modifier.padding(10.dp, (padding + 20).dp)) {
    Text(text = text,
        style = style,
        inlineContent = mapOf(
            TAG_IMAGE_URL to InlineTextContent(
                Placeholder(style.fontSize, style.fontSize, PlaceholderVerticalAlign.Bottom)
            ) {
                Image(
                    bitmap = BitmapFromImageURL(it),
                    contentDescription = null,
                    modifier = modifier
                )
            }
        )
    )
}