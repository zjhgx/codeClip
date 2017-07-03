package hql;

import java.util.List;

import com.upg.loan.mapping.LoanApplyCar;

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

}
