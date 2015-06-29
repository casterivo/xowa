/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012 gnosygnu@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gplx.xowa.files; import gplx.*; import gplx.xowa.*;
import gplx.ios.*;
import gplx.xowa.files.gui.*; import gplx.xowa.files.repos.*;
public class Xof_fsdb_itm implements Xof_file_itm {
	private int lnki_upright_patch;
	public byte[]				Lnki_wiki_abrv()			{return lnki_wiki_abrv;} private byte[] lnki_wiki_abrv;
	public int					Lnki_exec_tid()				{return lnki_exec_tid;} private int lnki_exec_tid;
	public byte[]				Lnki_ttl()					{return lnki_ttl;} private byte[] lnki_ttl;
	public byte					Lnki_type()					{return lnki_type;} private byte lnki_type;
	public double				Lnki_upright()				{return lnki_upright;} private double lnki_upright;
	public int					Lnki_w()					{return lnki_w;} private int lnki_w;
	public int					Lnki_h()					{return lnki_h;} private int lnki_h;
	public double				Lnki_time()					{return lnki_time;} private double lnki_time = Xof_lnki_time.Null;
	public int					Lnki_page()					{return lnki_page;} private int lnki_page = Xof_lnki_page.Null;
	public int					User_thumb_w()				{return user_thumb_w;} private int user_thumb_w = Xof_img_size.Thumb_width_img;
	public byte					Orig_repo_id()				{return orig_repo_id;} private byte orig_repo_id = Xof_repo_itm_.Repo_null;
	public byte[]				Orig_repo_name()			{return orig_repo_name;} private byte[] orig_repo_name;
	public byte[]				Orig_ttl()					{return orig_ttl;} private byte[] orig_ttl;
	public byte[]				Orig_ttl_md5()				{return orig_ttl_md5;} private byte[] orig_ttl_md5;
	public Xof_ext				Orig_ext()					{return orig_ext;} private Xof_ext orig_ext;
	public byte[]				Orig_redirect()				{return orig_redirect;} private byte[] orig_redirect;
	public int					Orig_w()					{return orig_w;} private int orig_w = Xop_lnki_tkn.Width_null;
	public int					Orig_h()					{return orig_h;} private int orig_h = Xop_lnki_tkn.Height_null;
	public int					Html_uid()					{return html_uid;} private int html_uid;
	public byte					Html_elem_tid()				{return html_elem_tid;} private byte html_elem_tid;
	public int					Html_w()					{return html_w;} private int html_w;
	public int					Html_h()					{return html_h;} private int html_h;
	public Io_url				Html_view_url()				{return html_view_url;} private Io_url html_view_url;
	public Io_url				Html_orig_url()				{return html_orig_url;} private Io_url html_orig_url = Io_url_.Empty;
	public int					Html_gallery_mgr_h()		{return html_gallery_mgr_h;} private int html_gallery_mgr_h = Int_.Neg1;
	public Js_img_wkr			Html_img_wkr()				{return html_img_wkr;} private Js_img_wkr html_img_wkr;
	public boolean					File_is_orig()				{return file_is_orig;} private boolean file_is_orig;
	public int					File_w()					{return file_w;} private int file_w;
	public long					File_size()					{return file_size;} private long file_size;
	public boolean					Meta_is_new()				{return false;}
	public boolean					Orig_exists()				{return orig_exists;} public void Orig_exists_y_() {orig_exists = Bool_.Y;} public void Orig_exists_n_() {orig_exists = Bool_.N;} private boolean orig_exists;
	public boolean					File_exists()				{return file_exists;} public void File_exists_y_() {file_exists = Bool_.Y;} public void File_exists_n_() {file_exists = Bool_.N;} public void File_exists_(boolean v) {file_exists = v;} private boolean file_exists;
	public boolean					File_resized()				{return file_resized;} public void File_resized_y_() {file_resized = Bool_.Y;} private boolean file_resized;
	public boolean					Fsdb_insert()				{return fsdb_insert;} public void Fsdb_insert_y_() {fsdb_insert = true;} private boolean fsdb_insert;
	public int					Xfer_idx()					{return xfer_idx;} private int xfer_idx;
	public int					Xfer_len()					{return xfer_len;} private int xfer_len;

	public void	Init_at_lnki(int exec_tid, byte[] wiki_abrv, byte[] lnki_ttl, byte lnki_type, double lnki_upright, int lnki_w, int lnki_h, double lnki_time, int lnki_page, int lnki_upright_patch) {
		this.lnki_exec_tid = exec_tid; this.lnki_wiki_abrv = wiki_abrv;
		this.lnki_type = lnki_type; this.lnki_upright = lnki_upright; this.lnki_upright_patch = lnki_upright_patch;
		this.lnki_w = lnki_w; this.lnki_h = lnki_h; this.lnki_time = lnki_time; this.lnki_page = lnki_page;			
		this.lnki_ttl = lnki_ttl;
		this.Orig_ttl_(lnki_ttl);	// default orig_ttl to lnki_ttl, since there are some classes that don't call Ctor_by_orig
		this.orig_ext = Xof_ext_.new_by_ttl_(lnki_ttl);
	}
	public void	Init_at_orig(byte orig_repo_id, byte[] orig_repo_name, byte[] orig_ttl, Xof_ext orig_ext, int orig_w, int orig_h, byte[] orig_redirect) {
		this.orig_repo_id = orig_repo_id; this.orig_repo_name = orig_repo_name; this.orig_redirect = orig_redirect;
		this.orig_w = orig_w; this.orig_h = orig_h;
		if		(Bry_.Len_gt_0(orig_redirect))				// redirect exists; EX: A.png redirected to B.png
			this.Orig_ttl_(orig_redirect);					// update fsdb with atrs of B.png
		else if	(!Bry_.Eq(lnki_ttl, orig_ttl))				// ttls differ; EX: "A_.png" vs "A.png"
			this.Orig_ttl_(orig_ttl);
		else
			this.Orig_ttl_(orig_ttl);
		this.orig_ext = orig_ext;							// NOTE: always use orig_ext since this comes directly from wmf_api; DATE:2015-05-17
	}
	public void Init_at_lnki_by_near(int file_w) {
		this.lnki_w = file_w; this.lnki_h = Xof_img_size.Size_null_deprecated;
	}
	public void	Init_at_html(int exec_tid, Xof_img_size img_size, Xof_repo_itm repo, Xof_url_bldr url_bldr) {
		Calc_html_size(exec_tid, img_size);
		this.html_view_url = url_bldr.To_url_trg(repo, this, file_is_orig);
		this.html_orig_url = url_bldr.To_url_trg(repo, this, Bool_.Y);
	}
	public void Init_at_gallery_bgn(int html_w, int html_h, int file_w) {
		this.html_w = html_w; this.html_h = html_h; 
		this.file_w = file_w;
	}
	public void Init_at_gallery_end(int html_w, int html_h, Io_url html_view_url, Io_url html_orig_url) {
		this.html_w = html_w; this.html_h = html_h; 
		this.html_view_url = html_view_url;
		this.html_orig_url = html_orig_url;
		this.file_exists = true;
	}
	public void Init_at_hdoc(int html_uid, byte html_elem_tid) {
		this.html_uid = html_uid; this.html_elem_tid = html_elem_tid;
	}
	public void	Init_at_fsdb_make
		( byte[] orig_ttl, int orig_ext_id	// NOTE:fsdb_make uses fields named lnki_ttl and lnki_ext_id, but really is orig_ttl and orig_ext_id
		, int lnki_w, int lnki_h, double lnki_time, int lnki_page
		, byte orig_repo_id, int orig_w, int orig_h, byte[] orig_redirect
		, boolean file_is_orig
		) {
		this.lnki_w = lnki_w; this.lnki_h = lnki_h; this.lnki_time = lnki_time; this.lnki_page = lnki_page;
		this.orig_repo_id = orig_repo_id; this.orig_w = orig_w; this.orig_h = orig_h;
		this.file_is_orig = file_is_orig;
		this.html_w = lnki_w; this.html_h = lnki_h;	// set html_size as file_size (may try to optimize later by removing similar thumbs (EX: 220,221 -> 220))
		this.Orig_ttl_(orig_ttl);
		this.orig_ext = Xof_ext_.new_by_id_(orig_ext_id);	// NOTE: data in fsdb_make may override lnki_ext; EX: ttl=A.png; but ext=".jpg"
	}
	public void Init_by_fsdb_near(boolean file_is_orig, int file_w) {
		this.file_is_orig = file_is_orig; this.file_w = file_w;
	}
	public void Init_at_xfer(int idx, int len) {
		this.xfer_idx = idx; this.xfer_len = len;
	}
	public void File_is_orig_(boolean v) {this.file_is_orig = v;}
	public void Orig_repo_name_(byte[] v) {orig_repo_name = v;}
	public void Html_elem_tid_(byte v) {this.html_elem_tid = v;}
	public void Html_size_(int w, int h) {html_w = w; html_h = h;}
	public void Html_view_url_(Io_url v) {html_view_url = v;}
	public void Html_orig_url_(Io_url v) {html_orig_url = v;}
	public void Html_img_wkr_(Js_img_wkr v) {html_img_wkr = v;}
	public void Html_gallery_mgr_h_(int v) {html_gallery_mgr_h = v;}
	public void File_size_(long v) {this.file_size = v;}
	private void Orig_ttl_(byte[] v) {
		this.orig_ttl = v;
		this.orig_ttl_md5 = Xof_file_wkr_.Md5_fast(v);
	}
	private void Lnki_time_validate() {
		if (!orig_ext.Id_is_media() && lnki_time != Xof_lnki_time.Null)	// file is not media, but has thumbtime; this check can't be moved to Lnki_time_() b/c it needs ext
			lnki_time = Xof_lnki_time.Null;								// set time to null; needed else url will reference thumbtime; PAGE:en.w:Moon; EX:[[File:Lunar libration with phase Oct 2007 450px.gif|thumbtime=0:02]]; DATE:2014-07-20
	}
	private void Calc_html_size(int exec_tid, Xof_img_size img_size) {
		this.Lnki_time_validate();
		if (orig_ext.Id_is_audio_strict())								// audio does not have html size calculated; everything else does
			file_is_orig = Bool_.Y;
		else {
			img_size.Html_size_calc(exec_tid, lnki_w, lnki_h, lnki_type, lnki_upright_patch, lnki_upright, orig_ext.Id(), orig_w, orig_h, Xof_img_size.Thumb_width_img);
			html_w = img_size.Html_w(); html_h = img_size.Html_h();
			file_w = img_size.File_w();
			file_is_orig = img_size.File_is_orig();
		}
	}
}
