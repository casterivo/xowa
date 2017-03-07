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
package gplx.xowa.addons.bldrs.files.missing_origs; import gplx.*; import gplx.xowa.*; import gplx.xowa.addons.*; import gplx.xowa.addons.bldrs.*; import gplx.xowa.addons.bldrs.files.*;
import gplx.dbs.*;
import gplx.xowa.bldrs.*; import gplx.xowa.bldrs.wkrs.*;	
import gplx.xowa.files.*; import gplx.xowa.files.repos.*; import gplx.xowa.files.origs.*; import gplx.xowa.apps.wms.apis.origs.*;
import gplx.xowa.addons.bldrs.files.dbs.*;
public class Xobldr_missing_origs_cmd extends Xob_cmd__base {
	private int fail_max = 100000;
	public Xobldr_missing_origs_cmd(Xob_bldr bldr, Xowe_wiki wiki) {super(bldr, wiki);}
	@Override public void Cmd_run() {
		// got orig_tbl
		Db_conn conn = Xob_db_file.New__file_make(wiki.Fsys_mgr().Root_dir()).Conn();
		Xob_orig_regy_tbl.Create_table(conn);

		// get counts; fail if too many
		int fail_count = conn.Exec_sql(Db_sql_.Make_by_fmt(String_.Ary("SELECT Count(lnki_ttl) FROM orig_regy WHERE orig_page_id IS NULL")));
		if (fail_count > fail_max) throw Err_.new_wo_type("bldr.find_missing: too many missing: missing=~{0} max=~{1}", fail_count, fail_max);
		Gfo_usr_dlg_.Instance.Note_many("", "", "bldr.find_missing: found=~{0}", fail_count);

		// select into list; ignore any which are invalid titles
		Ordered_hash list = Ordered_hash_.New_bry();
		int invalid_count = 0;
		String sql = "SELECT lnki_ttl FROM orig_regy WHERE orig_page_id IS NULL";
		Db_rdr rdr = conn.Stmt_sql(sql).Exec_select__rls_auto();
		try {
			while (rdr.Move_next()) {
				// get lnki_ttl; check if valid
				byte[] lnki_ttl = rdr.Read_bry("lnki_ttl");
				Xoa_ttl ttl = wiki.Ttl_parse(lnki_ttl);
				if (ttl == null) {
					invalid_count++;
					continue;
				}

				// create itm and add to list
				Xobldr_missing_origs_item itm = new Xobldr_missing_origs_item();
				itm.Init_by_orig_tbl(lnki_ttl);
				list.Add(itm.Lnki_ttl(), itm);
			}
		} finally {rdr.Rls();}
		Gfo_usr_dlg_.Instance.Note_many("", "", "bldr.find_missing: invalid=~{0}", invalid_count);

// call api with list
		Xobldr_missing_origs_wmfapi wmf_api = new Xobldr_missing_origs_wmfapi(wiki.App().Wmf_mgr().Download_wkr());
		int list_len = list.Len();
		int list_bgn = 0;
		// loop until no more entries
		while (true) {
			int list_end = list_bgn + 500;
			if (list_end > list_len) list_end = list_len;

			// find items
			wmf_api.Find_by_list(list, Xof_repo_tid_.Tid__remote, "commons.wikimedia.org", list_bgn);

			// loop list and update
			conn.Txn_bgn("bldr.find_missing");
			Db_stmt update_stmt = conn.Stmt_update("orig_regy", String_.Ary("lnki_ttl")
				, "orig_repo"
				, "orig_page_id", "orig_redirect_id", "orig_redirect_ttl"
				, "orig_file_id", "orig_file_ttl", "orig_file_ext"
				, "orig_size", "orig_w", "orig_h", "orig_media_type", "orig_minor_mime", "orig_timestamp");
			// , "orig_bits"
			for (int i = list_bgn; i < list_end; i++)  {
				Xobldr_missing_origs_item itm = (Xobldr_missing_origs_item)list.Get_at(i);
				update_stmt
					.Val_byte("orig_repo", itm.Orig_repo())
					.Val_int("orig_page_id", itm.Orig_page_id())
					.Val_int("orig_redirect_id", itm.Orig_redirect_id())
					.Val_bry_as_str("orig_redirect_ttl", itm.Orig_redirect_ttl())
					.Val_int("orig_file_id", itm.Orig_file_id())
					.Val_bry_as_str("orig_file_ttl", itm.Orig_file_ttl())
					.Val_int("orig_file_ext", itm.Orig_file_ext())
					.Val_int("orig_size", itm.Orig_size())
					.Val_int("orig_w", itm.Orig_w())
					.Val_int("orig_h", itm.Orig_h())
					.Val_bry_as_str("orig_media_type", itm.Orig_media_type())
					.Val_bry_as_str("orig_minor_mime", itm.Orig_minor_mime())
					.Val_bry_as_str("orig_timestamp", itm.Orig_timestamp())
					.Crt_bry_as_str("lnki_ttl", itm.Lnki_ttl()).Exec_update();
			}
			conn.Txn_end();

			// update bounds
			if (list_end == list_len) break;
			list_bgn += 500;
		}
	}
	@Override public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk__fail_max_))					this.fail_max = m.ReadInt("v");
		else													return super.Invk(ctx, ikey, k, m);
		return this;
	}	private static final String Invk__fail_max_ = "fail_max_";

	public static final String BLDR_CMD_KEY = "file.orig_regy.find_missing";
	@Override public String Cmd_key() {return BLDR_CMD_KEY;} 
	public static final    Xob_cmd Prototype = new Xobldr_missing_origs_cmd(null, null);
	@Override public Xob_cmd Cmd_clone(Xob_bldr bldr, Xowe_wiki wiki) {return new Xobldr_missing_origs_cmd(bldr, wiki);}
}