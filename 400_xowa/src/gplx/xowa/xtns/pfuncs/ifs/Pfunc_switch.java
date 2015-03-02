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
package gplx.xowa.xtns.pfuncs.ifs; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*; import gplx.xowa.xtns.pfuncs.*;
public class Pfunc_switch extends Pf_func_base {
	@Override public int Id() {return Xol_kwd_grp_.Id_xtn_switch;}
	@Override public Pf_func New(int id, byte[] name) {return new Pfunc_switch().Name_(name);}
	@Override public boolean Func_require_colon_arg() {return true;}
	@Override public void Func_evaluate(Xop_ctx ctx, byte[] src, Xot_invk caller, Xot_invk self, Bry_bfr bfr) {// REF.MW:ParserFunctions_body.php
		int self_args_len = self.Args_len(); if (self_args_len == 0) return;	// no cases; return; effectively "empty"
		byte[] argx = Eval_argx(ctx, src, caller, self);
		boolean fall_thru_found = false;
		byte[] match = null;
		Arg_itm_tkn dflt_val_tkn = null; byte[] dflt_val_bry = null;
		Arg_nde_tkn last_keyless_arg = null;
		Bry_bfr tmp = ctx.Wiki().Utl__bfr_mkr().Get_b512();
		Xol_kwd_mgr kwd_mgr = ctx.Lang().Kwd_mgr();
		for (int i = 0; i < self_args_len; i++) {
			Arg_nde_tkn arg = self.Args_get_by_idx(i);
			if (arg.KeyTkn_exists()) {								// = exists; EX: "|a=1|"
				last_keyless_arg = null;							// set last_keyless_arg to null
				byte[] case_key = Get_or_eval(ctx, src, caller, self, bfr, arg.Key_tkn(), tmp);
				if		(	fall_thru_found							// fall-thru found earlier; take cur value; EX: {{#switch:a|a|b=1|c=2}} -> 1
						||	Pf_func_.Eq_(case_key, argx)			// case_key matches argx; EX: {{#switch:a|a=1}}
					) {
					match = Get_or_eval(ctx, src, caller, self, bfr, arg.Val_tkn(), tmp);
					break;											// stop iterating; explicit match found;
				}
				else if (kwd_mgr.Kwd_default_match(case_key)){		// case_key is #default; EX: {{#switch:a|#default=1}}; note that "#defaultabc" is also allowed;
					dflt_val_tkn = arg.Val_tkn();					// set dflt_val_tkn; note that there is no "break" b/c multiple #defaults will use last one; EX: {{#switch:a|#default=1|#default=2}} -> 2
					dflt_val_bry = null;							// set dflt_val_bry to null; EX:{{#switch:a|#defaultabc|#default=2}} -> 2
				}
				else {}												// case_key != argx; continue
			}
			else {													// = missing; EX: "|a|", "|#default|"
				last_keyless_arg = arg;
				byte[] case_val = Get_or_eval(ctx, src, caller, self, bfr, arg.Val_tkn(), tmp);						
				if		(Pf_func_.Eq_(case_val, argx))				// argx matches case_val; EX: case_val="|a|" and argx="a"
					fall_thru_found = true;							// set as fall-thru; note that fall-thrus will have "val" in next keyed arg, so need to continue iterating; EX: {{#switch:a|a|b=1|c=2}} "a" is fall-thru, but "b" is next keyed arg with a val
				else if (kwd_mgr.Kwd_default_match(case_val)) {		// case_val starts with #default; EX: "|#default|" or "|#defaultabc|"
					last_keyless_arg = null;						// unflag last keyless arg else |#defaultabc| will be treated as last_keyless_arg and generate "#defaultabc"; DATE:2014-05-29
					dflt_val_tkn = null;							// unflag dflt_val_tkn; EX: {{#switch:a|b|#default=1|#default}} -> "" x> "1"
					int case_val_len = case_val.length;
					dflt_val_bry
						= case_val_len == Dflt_keyword_len			// PERF: check if case_val = "|#default|"
						? null										// PERF: set to null; don't create Bry_.Empty
						: Bry_.Mid(case_val, Dflt_keyword_len, case_val_len)	// chop off "#default"; EX: {{#switch:a|b|#defaultabc}} -> "abc"
						;					
				}
			}
		}
		if (match == null) {										// no match; will either use last_keyless arg or #default
			if		(last_keyless_arg != null)						// always prefer last_keyless_arg; EX: {{#switch:a|#default=1|2}} -> 2
				match = Get_or_eval(ctx, src, caller, self, bfr, last_keyless_arg.Val_tkn(), tmp);
			else if (dflt_val_bry != null)							// "|#defaultabc|" found; use it
				match = dflt_val_bry;
			else if (dflt_val_tkn != null)							// "|#default=val|" found; use it
				match = Get_or_eval(ctx, src, caller, self, bfr, dflt_val_tkn, tmp);
			else {}													// nothing found; noop; match will remain null
		}
		if (match != null)
			bfr.Add(match);
		tmp.Mkr_rls();
	}
	private byte[] Get_or_eval(Xop_ctx ctx, byte[] src, Xot_invk caller, Xot_invk self, Bry_bfr bb, Arg_itm_tkn itm, Bry_bfr tmp) {
		if (itm.Itm_static() == Bool_.Y_byte)
			return Bry_.Trim(src, itm.Dat_bgn(), itm.Dat_end());
		else {
			itm.Tmpl_evaluate(ctx, src, caller, tmp);
			return tmp.Xto_bry_and_clear_and_trim();
		}
	}
	public static final byte[] Dflt_keyword = Bry_.new_utf8_("#default");	// NOTE: technically should pull from messages, but would need to cache Dflt_keyword on wiki level; checked all Messages files, and no one overrides it; DATE:2014-05-29
	private static int Dflt_keyword_len = Dflt_keyword.length;
}
