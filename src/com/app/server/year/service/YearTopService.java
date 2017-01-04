package com.app.server.year.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.app.server.comm.entity.Where;
import com.app.server.comm.handle.JdbcHandle;
import com.app.server.comm.util.FileUtils;
import com.app.server.year.domain.BankYearTop;
import com.app.server.year.domain.YearTop;

@Service
public class YearTopService {

	@Resource
	JdbcHandle jdbcHandle;
	
	public void parseTop(String date){
		List<YearTop> tops=loadYearTop(date); 
		List<BankYearTop> banks=loadBanks(date);
		for(int i=20;i<50;i++){
			BankYearTop bank=banks.get(i-20);
			YearTop top=null;
			if(i>tops.size()-1){
				top=new YearTop();
				top.setDate(date);
				top.setMatchId(1);
				top.setTotalInVote(0);
				top.setHasReset(0);
				top.setIsExpire(0);
				top.setScore(2100 - 100 * (i + 1));
				top.setTotalInVote(0);
				top.setUserLevel(15);
			}else{
				top=tops.get(i);
			}
			top.setUid(Integer.valueOf(bank.getUid()));
			top.setInVote(Integer.valueOf(bank.getVote()));
			jdbcHandle.saveOrUpdateAuto(top);
		}
	}
	
	private List<BankYearTop> loadBanks(String date){
		String str=FileUtils.readFile("D:/test/"+date+".txt");
		String [] tabs=str.split("\r\n");
		List<BankYearTop> tops=new ArrayList<BankYearTop>();
		for(String line:tabs){
			String [] lineTab=line.split("\t");
			if(lineTab.length==2){
				BankYearTop bank=new BankYearTop();
				bank.setUid(lineTab[0]);
				bank.setVote(lineTab[1]);
				tops.add(bank);
			}
		}
		return tops;
	}
	private List<YearTop> loadYearTop(String date){
		Where where =new Where();
		where.set("date", date);
		where.set("matchId", 1);
		return (List<YearTop>) jdbcHandle.findBean(YearTop.class, where,"inVote",true);
	}
}
