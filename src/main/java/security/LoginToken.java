package security;

import java.util.Arrays;
import java.util.Calendar;

import cn.vobile.bean.User;
import cn.vobile.common.Constants;
import cn.vobile.common.MD5;
import cn.vobile.login.LoginRequest;
import cn.vobile.login.LoginRequestResult;
import cn.vobile.webservice.HttpServletRequest;

public class LoginToken {
	   public static String generateAuthenToken(String userId, int companyId) {
	        String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
	        int nonce = (int) (Math.random() * 100);
	        String[] str = { timestamp, String.valueOf(companyId), String.valueOf(nonce) };
	        Arrays.sort(str);
	        String authenToken = new MD5().MD5(str[0] + str[1] + str[2]);
	        if (authenToken.length() < AUTNEN_TOKEN_LENGTH) {
	            authenToken += authenToken.substring(0, AUTNEN_TOKEN_LENGTH - authenToken.length());
	        } else if (authenToken.length() > AUTNEN_TOKEN_LENGTH) {
	            authenToken = authenToken.substring(0, AUTNEN_TOKEN_LENGTH);
	        }
	        return (userId + authenToken);
	    }
	   
	   public void authenAndSaveUserInfo(LoginRequest loginRequest, LoginRequestResult result, HttpServletRequest req) {
	        String userName = loginRequest.getUser_name();
	        User user = userService.getUser(userName);
	        if (user == null || user.getPassword() == null 
	                || !(new MD5().MD5(loginRequest.getPassword()).equals(user.getPassword()))) {
	            User loginUser = (User) redisService.getObjectFromRedis(userName + loginFailSuffix, User.class);           
	            if (loginUser != null) {
	                loginUser.setLoginCount(loginUser.getLoginCount() + 1);
	            } else {
	                loginUser = new User();
	                loginUser.setUsername(userName);
	            }
	            redisService.setObjectToRedis(userName + loginFailSuffix, loginUser, Constants.LOGIN_RETRY_INTERVAL);
	            result.setReturn_code(HttpStatus.SC_UNAUTHORIZED);
	            result.setMessage("Bad authentication");
	        } else {
	            String userId = String.valueOf(user.getId());
	            User cacheUser =  (User) redisService.getObjectFromRedis(String.valueOf(userId) + Constants.LOGIN_SUCCESS_SUFFIX, User.class);
	            
	            redisService.removeInfoFromRedis(userName + loginFailSuffix);
	            if (cacheUser != null) {
	                result.setAuthenToken(cacheUser.getAuthenToken());
	                redisService.setKeyExpire(String.valueOf(userId) + Constants.LOGIN_SUCCESS_SUFFIX, Constants.AUTHEN_TOKEN_EXPIRETIME);
	            } else {
	                String authenToken = Constants.generateAuthenToken(userId, user.getCompanyId());
	                user.setAuthenToken(authenToken);
	                redisService.setObjectToRedis(String.valueOf(userId) + Constants.LOGIN_SUCCESS_SUFFIX, user, Constants.AUTHEN_TOKEN_EXPIRETIME);
	                result.setAuthenToken(user.getAuthenToken());
	                logger.info("authenticated user :" + user.toString());
	            }
	        }
	    }
	    	   
}
