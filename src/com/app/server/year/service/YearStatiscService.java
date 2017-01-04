package com.app.server.year.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.app.server.comm.entity.Where;
import com.app.server.comm.handle.JdbcHandle;
import com.app.server.comm.util.DateUtils;
import com.app.server.comm.util.FileUtils;
import com.app.server.year.domain.YearUser;

@Service
public class YearStatiscService {

	@Resource
	JdbcHandle jdbcHandle;
	
	public void parseYearTop(){
		List<YearUser> users=loadUsers();
		Date startDate=DateUtils.stringToDate("2016-12-12 00:00:00", DateUtils.DATETIME_PATTERN);
		while(!DateUtils.dateToString(startDate).equals("2016-12-19")){
			for(YearUser user:users){
				Integer totalVote=getDayTotalVote(user.getUid(), startDate);
				FileUtils.writeAppend("d://"+DateUtils.dateToString(startDate)+".txt", user.getUid()+":"+totalVote+"\r\n");
			}
			startDate=DateUtils.addDate(startDate, 1);
		}
		
	}
	
	public Integer getDayTotalVote(Integer uid,Date date){
		String sql="select sum(vote) from year_vote_record where targeUid=? and createDate>? and createDate>? ";
		return jdbcHandle.doQueryInteger(sql, uid,DateUtils.getDayFirstTime(date),DateUtils.getDayLastTime(date));
	}
	public List<YearUser> loadUsers(){
		Where where=new Where();
		where.set("totalInVote", ">",0);
		return (List<YearUser>) jdbcHandle.findBean(YearUser.class,where);
	}
}
