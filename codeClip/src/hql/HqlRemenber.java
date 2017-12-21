package hql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cs.lexiao.business.mapping.LxPlatform;
import com.cs.lexiao.business.platform.model.PlatformSearchBean;
import com.upg.loan.mapping.LoanApplyCar;
import com.upg.loan.thirdparty.bean.ZFOrderSearchBean;
import com.upg.ucars.framework.base.Page;
import com.upg.uwc.mapping.es.EsAccount;

public class HqlRemenber {
	@Override
	public LoanApplyCar getLoanApplyCarByApplyId(Integer applyId) {
		LoanApplyCar  loanApplyCar = null;
	    String hql = "select new LoanApplyCar(b.nakedBikeAmount, c.name) from LoanApply a, LoanApplyCar b, CarSalesOrganization c where a.id=b.loanApplyId and b.salesOrganization=c.id and a.id=? order by b.id desc";
	    List<LoanApplyCar> list = find(hql, applyId);
	    if (list != null && list.size() > 0) {
	    	loanApplyCar = list.get(0);
	    }
	    return loanApplyCar;	
	}
	
	
	@Override
	public List<LxPlatform> getPlatformList(PlatformSearchBean searchBean) {
		
		StringBuffer sf = new StringBuffer("from LxPlatform where 1=1");
		List<Object> paramList = new ArrayList<Object>();
		
		if(searchBean.getName()!=null){
			sf.append(" and platName like ?");
			paramList.add("%"+searchBean.getName()+"%");
		}
		
		if(searchBean.getStatus()!=null){
			sf.append(" and status=?");
			paramList.add(searchBean.getStatus());
		}
		List<LxPlatform> list = find(sf.toString(),paramList.toArray());
		return list;
	}
	
	
	@Override
	public List<LxPlatform> getPlatformList(PlatformSearchBean searchBean,
			Page page) {
		StringBuffer sf = new StringBuffer("from LxPlatform where 1=1");
		Map<String,Object> parameterMap = new HashMap<String, Object>();
		
		if(searchBean.getName()!=null){
			sf.append(" and platName like :plaformName");
			parameterMap.put("plaformName","%"+searchBean.getName()+"%");
		}
		
		if(searchBean.getStatus()!=null && searchBean.getStatus()!=0){
			sf.append(" and status=:status");
			parameterMap.put("status",searchBean.getStatus());
		}
		return queryByParam(sf.toString(), parameterMap, page);	

	}
	
	
	@Override
	public List<Map<String, Object>> findFinanceApplyByCondition(ZFOrderSearchBean searchBean, Page pg) {
		  StringBuffer sql =   new StringBuffer("")
          .append("SELECT record.id,record.order_id,record.amount,record.`status`,record.is_manual," +
				  "record.repay_error_info,record.create_time,record.pay_date,apply.mobile,apply.name " +
				  "really_name,CONCAT(LEFT(card.card_id,6),'********',RIGHT(card.card_id,4)) AS card_id," +
				  "t.code_name FROM jk_loan_repay_record record " +
				  "LEFT JOIN jk_user_basic usr ON record.uid=usr.id " +
				  "LEFT JOIN jk_user_bank_card card ON record.bank_card_id=card.card_id " +
				  "LEFT JOIN jk_code t ON t.code_key='E162' and t.code_no=record.`status` " +
				  "LEFT JOIN jk_loan_apply apply on record.loan_apply_id=apply.id WHERE 1=1 ");
		  Map<String,Object> params = new HashMap<String,Object>();
		  Long repayInfoId = searchBean.getRepayInfoId();
			
			String name = searchBean.getName();
			if (StringUtils.isNotBlank(name)) {
				sql.append("AND usr.name like :name \n");
	            params.put("name", "%"+name.trim() + "%");
			}
			String mobile = searchBean.getMobile();
			if (StringUtils.isNotBlank(mobile)) {
				sql.append("AND usr.mobile like :mobile \n");
	            params.put("mobile","%"+mobile.trim() + "%");
			}
			if (repayInfoId!=null) {
				sql.append("AND record.repay_info_id=:repayInfoId \n");
	            params.put("repayInfoId", repayInfoId);
			}
			Byte status = searchBean.getStatus();
			if (status!=null) {
				sql.append("AND record.`status`=:status \n");
	            params.put("status",status);
			}
			sql.append("order by record.id desc");
			List<Map<String, Object>> data = getMapListByStanderdSQL(sql.toString(),params,pg);
		return data;
	}
	


	
	
}
