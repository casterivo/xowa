/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012-2017 gnosygnu@gmail.com

XOWA is licensed under the terms of the General Public License (GPL) Version 3,
or alternatively under the terms of the Apache License Version 2.0.

You may use XOWA according to either of these licenses as is most appropriate
for your project on a case-by-case basis.

The terms of each license can be found in the source code repository:

GPLv3 License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-GPLv3.txt
Apache License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-APACHE2.txt
*/
package gplx.xowa.addons.wikis.searchs.fulltexts.finders; import gplx.*; import gplx.xowa.*; import gplx.xowa.addons.*; import gplx.xowa.addons.wikis.*; import gplx.xowa.addons.wikis.searchs.*; import gplx.xowa.addons.wikis.searchs.fulltexts.*;
import gplx.xowa.guis.cbks.*;
import gplx.core.btries.*;
import gplx.xowa.addons.wikis.searchs.searchers.crts.*;
public class Xosearch_finder_mgr {
	private Btrie_slim_mgr hook_trie;
	private Xosearch_word_node tree_root;
	private final    Srch_crt_parser parser = new Srch_crt_parser(Srch_crt_scanner_syms.Dflt);
	private final    Btrie_rv trv = new Btrie_rv();

	public byte[] Query() {return query;} private byte[] query;
	public void Init(byte[] query, boolean case_match, boolean auto_wildcard, byte wildchar_byte, byte not_byte) {
		this.query = query;
		// create a new hook_trie based on case_match
		this.hook_trie = case_match ? Btrie_slim_mgr.cs() : Btrie_slim_mgr.ci_u8();

		// create a new tree_root for eval
		this.tree_root = Xosearch_word_node_.New_root(parser.Parse_or_invalid(query, auto_wildcard).Root, hook_trie, wildchar_byte, not_byte);
	}
	public void Match(byte[] src, int src_bgn, int src_end, Xosearch_finder_cbk cbk) {
		// init and clear
		int cur = 0;
		tree_root.Clear();

		// scan through text one-byte at a time
		// NOTE: skipping ahead to word-start instead of going byte-by-byte may seem more performant, but will still need to do substring analysis b/c of wildcards and punctuation; EX: "abc" and " 'abc' "; "*abc" and " xyzabc. "
		while (cur <= src_end) {
			// check each byte against hook_trie
			Object hook_obj = hook_trie.Match_at(trv, src, cur, src_end);

			// current byte matches no hooks; go to next byte
			if (hook_obj == null) {
				cur++;
				continue;
			}

			// current byte matches a hook; get hook and hook_end
			Xosearch_word_node hook = (Xosearch_word_node)hook_obj;
			int hook_end = cur + hook.word_hook.length;

			// get current word bounds by finding flanking ws
			int word_bgn = Bry_find_.Find_bwd_ws(src, cur, 0) + 1;
			int word_end = Bry_find_.Find_fwd_until_ws(src, hook_end, src_end);
			if (word_end == -1) word_end = src_end;	// WORKAROUND: no match returns -1 instead of src_end

			// check if current word matches criteria-word
			if (hook.Match_word(src, cur, hook_end, word_bgn, word_end)) {
				cbk.Process_item_found(src, cur, hook_end, word_bgn, word_end, hook);
			}

			// update position to word_end
			cur = word_end;
		}

		// mark page done
		cbk.Process_page_done(src, tree_root);
	}
}