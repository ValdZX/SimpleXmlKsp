package ua.vald_zx.simplexml.ksp.xml.utils

internal interface UnEscape {
    fun unescape(input: String): String
}

internal object Escaping {
    fun String.unescapeXml(): String {
        val text = this
        val result = StringBuilder(text.length)
        var i = 0
        val n = text.length
        while (i < n) {
            val charAt = text[i]
            if (charAt != Constants.CHAR_AMPERSAND) {
                result.append(charAt)
                i++
            } else {
                when {
                    text.startsWith(Constants.ENCODED_AMPERSAND, i) -> {
                        result.append(Constants.CHAR_AMPERSAND)
                        i += 5
                    }
                    text.startsWith(Constants.ENCODED_SINGLE_QUOTE, i) -> {
                        result.append(Constants.CHAR_SINGLE_QUOTE)
                        i += 6
                    }
                    text.startsWith(Constants.ENCODED_DOUBLE_QUOTE, i) -> {
                        result.append(Constants.CHAR_DOUBLE_QUOTE)
                        i += 6
                    }
                    text.startsWith(Constants.ENCODED_LESS_THAN, i) -> {
                        result.append(Constants.CHAR_LESS_THAN)
                        i += 4
                    }
                    text.startsWith(Constants.ENCODED_GREATER_THAN, i) -> {
                        result.append(Constants.CHAR_GREATER_THAN)
                        i += 4
                    }
                    text.startsWith(Constants.ENCODED_UTF8, i) -> {
                        val index = text.indexOf(';', i)
                        result.append(charFromDecimal(text.substring(i + 2, index)))
                        i = index + 1
                    }
                    else -> i++
                }
            }
        }
        return result.toString()
    }

    // https://www.freeformatter.com/html-entities.html
    private val NAMED_HTML_ENTITIES: Map<String, String> = Builder.newMutableMap<String, String>()
        .put("&amp;", "&").put("&lt;", "<").put("&gt;", ">").put("&Agrave;", "??")
        .put("&Aacute;", "??").put("&Acirc;", "??").put("&Atilde;", "??").put("&Auml;", "??")
        .put("&Aring;", "??").put("&AElig;", "??").put("&Ccedil;", "??").put("&Egrave;", "??")
        .put("&Eacute;", "??").put("&Ecirc;", "??").put("&Euml;", "??").put("&Igrave;", "??")
        .put("&Iacute;", "??").put("&Icirc;", "??").put("&Iuml;", "??").put("&ETH;", "??")
        .put("&Ntilde;", "??").put("&Ograve;", "??").put("&Oacute;", "??").put("&Ocirc;", "??")
        .put("&Otilde;", "??").put("&Ouml;", "??").put("&Oslash;", "??").put("&Ugrave;", "??")
        .put("&Uacute;", "??").put("&Ucirc;", "??").put("&Uuml;", "??").put("&Yacute;", "??")
        .put("&THORN;", "??").put("&szlig;", "??").put("&agrave;", "??").put("&aacute;", "??")
        .put("&acirc;", "??").put("&atilde;", "??").put("&auml;", "??").put("&aring;", "??")
        .put("&aelig;", "??").put("&ccedil;", "??").put("&egrave;", "??").put("&eacute;", "??")
        .put("&ecirc;", "??").put("&euml;", "??").put("&igrave;", "??").put("&iacute;", "??")
        .put("&icirc;", "??").put("&iuml;", "??").put("&eth;", "??").put("&ntilde;", "??")
        .put("&ograve;", "??").put("&oacute;", "??").put("&ocirc;", "??").put("&otilde;", "??")
        .put("&ouml;", "??").put("&oslash;", "??").put("&ugrave;", "??").put("&uacute;", "??")
        .put("&ucirc;", "??").put("&uuml;", "??").put("&yacute;", "??").put("&thorn;", "??")
        .put("&yuml;", "??").put("&nbsp;", " ").put("&iexcl;", "??").put("&cent;", "??")
        .put("&pound;", "??").put("&curren;", "??").put("&yen;", "??").put("&brvbar;", "??")
        .put("&sect;", "??").put("&uml;", "??").put("&copy;", "??").put("&ordf;", "??")
        .put("&laquo;", "??").put("&not;", "??").put("&shy;", "\u00ad").put("&reg;", "??")
        .put("&macr;", "??").put("&deg;", "??").put("&plusmn;", "??").put("&sup2;", "??")
        .put("&sup3;", "??").put("&acute;", "??").put("&micro;", "??").put("&para;", "??")
        .put("&cedil;", "??").put("&sup1;", "??").put("&ordm;", "??").put("&raquo;", "??")
        .put("&frac14;", "??").put("&frac12;", "??").put("&frac34;", "??").put("&iquest;", "??")
        .put("&times;", "??").put("&divide;", "??").put("&forall;", "???").put("&part;", "???")
        .put("&exist;", "???").put("&empty;", "???").put("&nabla;", "???").put("&isin;", "???")
        .put("&notin;", "???").put("&ni;", "???").put("&prod;", "???").put("&sum;", "???")
        .put("&minus;", "???").put("&lowast;", "???").put("&radic;", "???").put("&prop;", "???")
        .put("&infin;", "???").put("&ang;", "???").put("&and;", "???").put("&or;", "???")
        .put("&cap;", "???").put("&cup;", "???").put("&int;", "???").put("&there4;", "???")
        .put("&sim;", "???").put("&cong;", "???").put("&asymp;", "???").put("&ne;", "???")
        .put("&equiv;", "???").put("&le;", "???").put("&ge;", "???").put("&sub;", "???")
        .put("&sup;", "???").put("&nsub;", "???").put("&sube;", "???").put("&supe;", "???")
        .put("&oplus;", "???").put("&otimes;", "???").put("&perp;", "???").put("&sdot;", "???")
        .put("&Alpha;", "??").put("&Beta;", "??").put("&Gamma;", "??").put("&Delta;", "??")
        .put("&Epsilon;", "??").put("&Zeta;", "??").put("&Eta;", "??").put("&Theta;", "??")
        .put("&Iota;", "??").put("&Kappa;", "??").put("&Lambda;", "??").put("&Mu;", "??")
        .put("&Nu;", "??").put("&Xi;", "??").put("&Omicron;", "??").put("&Pi;", "??")
        .put("&Rho;", "??").put("&Sigma;", "??").put("&Tau;", "??").put("&Upsilon;", "??")
        .put("&Phi;", "??").put("&Chi;", "??").put("&Psi;", "??").put("&Omega;", "??")
        .put("&alpha;", "??").put("&beta;", "??").put("&gamma;", "??").put("&delta;", "??")
        .put("&epsilon;", "??").put("&zeta;", "??").put("&eta;", "??").put("&theta;", "??")
        .put("&iota;", "??").put("&kappa;", "??").put("&lambda;", "??").put("&mu;", "??")
        .put("&nu;", "??").put("&xi;", "??").put("&omicron;", "??").put("&pi;", "??")
        .put("&rho;", "??").put("&sigmaf;", "??").put("&sigma;", "??").put("&tau;", "??")
        .put("&upsilon;", "??").put("&phi;", "??").put("&chi;", "??").put("&psi;", "??")
        .put("&omega;", "??").put("&thetasym;", "??").put("&upsih;", "??").put("&piv;", "??")
        .put("&OElig;", "??").put("&oelig;", "??").put("&Scaron;", "??").put("&scaron;", "??")
        .put("&Yuml;", "??").put("&fnof;", "??").put("&circ;", "??").put("&tilde;", "??")
        .put("&ensp;", "\u2002").put("&emsp;", "\u2003").put("&thinsp;", "\u2009").put("&zwnj;", "\u200c")
        .put("&zwj;", "\u200d").put("&lrm;", "\u200e").put("&rlm;", "\u200f").put("&ndash;", "???")
        .put("&mdash;", "???").put("&lsquo;", "???").put("&rsquo;", "???").put("&sbquo;", "???")
        .put("&ldquo;", "???").put("&rdquo;", "???").put("&bdquo;", "???").put("&dagger;", "???")
        .put("&Dagger;", "???").put("&bull;", "???").put("&hellip;", "???").put("&permil;", "???")
        .put("&prime;", "???").put("&Prime;", "???").put("&lsaquo;", "???").put("&rsaquo;", "???")
        .put("&oline;", "???").put("&euro;", "???").put("&trade;", "???").put("&larr;", "???")
        .put("&uarr;", "???").put("&rarr;", "???").put("&darr;", "???").put("&harr;", "???")
        .put("&crarr;", "???").put("&lceil;", "???").put("&rceil;", "???").put("&lfloor;", "???")
        .put("&rfloor;", "???").put("&loz;", "???").put("&spades;", "???").put("&clubs;", "???")
        .put("&hearts;", "???").put("&diams;", "???").build()

    fun String.unescapeHtml(): String {
        val text = this
        val result = StringBuilder(text.length)
        var i = 0
        while (i < text.length) {
            val charAt = text[i]
            if (charAt != Constants.CHAR_AMPERSAND) {
                result.append(charAt)
                i++
                continue
            }
            val index = text.indexOf(';', i)
            if (index == -1) {
                result.append("&")
                i++
                continue
            }
            val entity = text.substring(i, index + 1)
            val decode = NAMED_HTML_ENTITIES[entity]
            if (decode != null) {
                result.append(decode)
                i = index + 1
                continue
            }
            if (text[i + 1] == '#') {
                val real = if (text[i + 2] == 'x') charFromHex(
                    text.substring(
                        i + 3,
                        index
                    )
                ) else charFromDecimal(text.substring(i + 2, index))
                result.append(real)
                i = index + 1
                continue
            }
            result.append("&")
            i++
        }
        return result.toString()
    }

    private fun charFromDecimal(substring: String): Char {
        return substring.toInt().toChar()
    }

    private fun charFromHex(substring: String): Char {
        return substring.toInt(16).toChar()
    }
}