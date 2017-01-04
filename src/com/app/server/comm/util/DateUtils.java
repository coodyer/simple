package com.app.server.comm.util;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 日期类型与字符串类型相互转换
 */
public class DateUtils {
	/*
	 * 使用一个 time pattern 字符串指定时间格式。 在这种方式下，所有的 ASCII 字母被保留为模式字母，定义如下：
	 * 
	 * 符号 含义 表示 示例 ------ ------- ------------ ------- G 年代标志符 (Text) AD y 年
	 * (Number) 1996 M 月 (Text & Number) July & 07 d 日 (Number) 10 h 时 在上午或下午
	 * (1~12) (Number) 12 H 时 在一天中 (0~23) (Number) 0 m 分 (Number) 30 s 秒
	 * (Number) 55 S 毫秒 (Number) 978 E 星期 (Text) Tuesday D 一年中的第几天 (Number) 189
	 * F 一月中第几个星期几 (Number) 2 (2nd Wed in July) w 一年中第几个星期 (Number) 27 W
	 * 一月中第几个星期 (Number) 2 a 上午 / 下午 标记符 (Text) PM k 时 在一天中 (1~24) (Number) 24 K
	 * 时 在上午或下午 (0~11) (Number) 0 z 时区 (Text) Pacific Standard Time ' 文本转义符
	 * (Delimiter) '' 单引号 (Literal) '
	 * 
	 * 模式字母的数目决定了格式。
	 */
	/**
	 * Base ISO 8601 Date format yyyyMMdd i.e., 20021225 for the 25th day of
	 * December in the year 2002
	 */
	public static final String ISO_DATE_FORMAT = "yyyyMMdd";

	/**
	 * Expanded ISO 8601 Date format yyyy-MM-dd i.e., 2002-12-25 for the 25th
	 * day of December in the year 2002
	 */
	public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * yyyy-MM-dd hh:mm:ss
	 */
	public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyy-MM-dd hh:mm:ss
	 */
	public static String NUMBER_PATTERN = "yyyyMMddHHmmss";
	/**
	 * Default lenient setting for getDate.
	 */
	private static boolean LENIENT_DATE = false;
	public static final int START_OF_DATE = 0;
	public static final int END_OF_DATE = 1;
	public static final int ANYTIME_OF_DATE = 2;

	/**
	 * 暂时不用
	 * 
	 * @param JD
	 * @return
	 */
	protected static final float normalizedJulian(float JD) {

		float f = Math.round(JD + 0.5f) - 0.5f;

		return f;
	}

	/**
	 * 浮点值转换成日期格式<br>
	 * 暂时不用 Returns the Date from a julian. The Julian date will be converted to
	 * noon GMT, such that it matches the nearest half-integer (i.e., a julian
	 * date of 1.4 gets changed to 1.5, and 0.9 gets changed to 0.5.)
	 * 
	 * @param JD
	 *            the Julian date
	 * @return the Gregorian date
	 */
	public static final Date toDate(float JD) {

		/*
		 * To convert a Julian Day Number to a Gregorian date, assume that it is
		 * for 0 hours, Greenwich time (so that it ends in 0.5). Do the
		 * following calculations, again dropping the fractional part of all
		 * multiplicatons and divisions. Note: This method will not give dates
		 * accurately on the Gregorian Proleptic Calendar, i.e., the calendar
		 * you get by extending the Gregorian calendar backwards to years
		 * earlier than 1582. using the Gregorian leap year rules. In
		 * particular, the method fails if Y<400.
		 */
		float Z = (normalizedJulian(JD)) + 0.5f;
		float W = (int) ((Z - 1867216.25f) / 36524.25f);
		float X = (int) (W / 4f);
		float A = Z + 1 + W - X;
		float B = A + 1524;
		float C = (int) ((B - 122.1) / 365.25);
		float D = (int) (365.25f * C);
		float E = (int) ((B - D) / 30.6001);
		float F = (int) (30.6001f * E);
		int day = (int) (B - D - F);
		int month = (int) (E - 1);

		if (month > 12) {
			month = month - 12;
		}

		int year = (int) (C - 4715); // (if Month is January or February) or
										// C-4716 (otherwise)

		if (month > 2) {
			year--;
		}

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1); // damn 0 offsets
		c.set(Calendar.DATE, day);

		return c.getTime();
	}

	/**
	 * Returns the days between two dates. Positive values indicate that the
	 * second date is after the first, and negative values indicate, well, the
	 * opposite. Relying on specific times is problematic.
	 * 
	 * @param early
	 *            the "first date"
	 * @param late
	 *            the "second date"
	 * @return the days between the two dates
	 */
	public static final int daysBetween(Date early, Date late) {

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(early);
		c2.setTime(late);

		return daysBetween(c1, c2);
	}

	/**
	 * Returns the days between two dates. Positive values indicate that the
	 * second date is after the first, and negative values indicate, well, the
	 * opposite.
	 * 
	 * @param early
	 * @param late
	 * @return the days between two dates.
	 */
	public static final int daysBetween(Calendar early, Calendar late) {

		return (int) (toJulian(late) - toJulian(early));
	}

	/**
	 * Return a Julian date based on the input parameter. This is based from
	 * calculations found at <a
	 * href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day
	 * Calculations (Gregorian Calendar)</a>, provided by Bill Jeffrys.
	 * 
	 * @param c
	 *            a calendar instance
	 * @return the julian day number
	 */
	public static final float toJulian(Calendar c) {

		int Y = c.get(Calendar.YEAR);
		int M = c.get(Calendar.MONTH);
		int D = c.get(Calendar.DATE);
		int A = Y / 100;
		int B = A / 4;
		int C = 2 - A + B;
		float E = (int) (365.25f * (Y + 4716));
		float F = (int) (30.6001f * (M + 1));
		float JD = C + D + E + F - 1524.5f;

		return JD;
	}

	/**
	 * 暂时不用 Return a Julian date based on the input parameter. This is based
	 * from calculations found at <a
	 * href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day
	 * Calculations (Gregorian Calendar)</a>, provided by Bill Jeffrys.
	 * 
	 * @param date
	 * @return the julian day number
	 */
	public static final float toJulian(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return toJulian(c);
	}

	/**
	 * 日期增加
	 * 
	 * @param isoString
	 *            日期字符串
	 * @param fmt
	 *            格式
	 * @param field
	 *            年/月/日 Calendar.YEAR/Calendar.MONTH/Calendar.DATE
	 * @param amount
	 *            增加数量
	 * @return
	 * @throws ParseException
	 */
	public static final String dateIncrease(String isoString, String fmt,
			int field, int amount) {

		try {
			Calendar cal = GregorianCalendar.getInstance(TimeZone
					.getTimeZone("GMT"));
			cal.setTime(stringToDate(isoString, fmt, true));
			cal.add(field, amount);

			return dateToString(cal.getTime(), fmt);

		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Time Field Rolling function. Rolls (up/down) a single unit of time on the
	 * given time field.
	 * 
	 * @param isoString
	 * @param field
	 *            the time field.
	 * @param up
	 *            Indicates if rolling up or rolling down the field value.
	 * @param expanded
	 *            use formating char's
	 * @exception ParseException
	 *                if an unknown field value is given.
	 */
	public static final String roll(String isoString, String fmt, int field,
			boolean up) throws ParseException {

		Calendar cal = GregorianCalendar.getInstance(TimeZone
				.getTimeZone("GMT"));
		cal.setTime(stringToDate(isoString, fmt));
		cal.roll(field, up);

		return dateToString(cal.getTime(), fmt);
	}

	/**
	 * Time Field Rolling function. Rolls (up/down) a single unit of time on the
	 * given time field.
	 * 
	 * @param isoString
	 * @param field
	 *            the time field.
	 * @param up
	 *            Indicates if rolling up or rolling down the field value.
	 * @exception ParseException
	 *                if an unknown field value is given.
	 */
	public static final String roll(String isoString, int field, boolean up)
			throws ParseException {

		return roll(isoString, DATETIME_PATTERN, field, up);
	}

	/**
	 * 字符串转换为日期java.util.Date
	 * 
	 * @param dateText
	 *            字符串
	 * @param format
	 *            日期格式
	 * @param lenient
	 *            日期越界标志
	 * @return
	 */
	public static Date stringToDate(String dateText, String format,
			boolean lenient) {

		if (dateText == null) {

			return null;
		}

		DateFormat df = null;

		try {

			if (format == null) {
				df = new SimpleDateFormat();
			} else {
				df = new SimpleDateFormat(format);
			}

			// setLenient avoids allowing dates like 9/32/2001
			// which would otherwise parse to 10/2/2001
			df.setLenient(false);

			return df.parse(dateText);
		} catch (ParseException e) {

			return null;
		}
	}

	/**
	 * 字符串转换为日期java.util.Date
	 * 
	 * @param dateText
	 *            字符串
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static Date stringToDate(String dateString, String format) {

		return stringToDate(dateString, format, LENIENT_DATE);
	}

	/**
	 * 字符串转换为日期java.util.Date
	 * 
	 * @param dateText
	 *            字符串
	 */
	public static Date stringToDate(String dateString) {

		return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
	}

	/**
	 * 根据时间变量返回时间字符串
	 * 
	 * @return 返回时间字符串
	 * @param pattern
	 *            时间字符串样式
	 * @param date
	 *            时间变量
	 */
	public static String dateToString(Date date, String pattern) {

		if (date == null) {

			return null;
		}

		try {

			SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
			// sfDate.setLenient(false);

			return sfDate.format(date);
		} catch (Exception e) {

			return null;
		}
	}

	/**
	 * 根据时间变量返回时间字符串 yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
	}

	/**
	 * 返回当前时间
	 * 
	 * @return 返回当前时间
	 */
	public static Date getCurrentDateTime() {
		java.util.Calendar calNow = java.util.Calendar.getInstance();
		java.util.Date dtNow = calNow.getTime();

		return dtNow;
	}

	/**
	 * 返回当前日期字符串
	 * 
	 * @param pattern
	 *            日期字符串样式
	 * @return
	 */
	public static String getCurrentDateString(String pattern) {
		return dateToString(getCurrentDateTime(), pattern);
	}

	/**
	 * 返回当前日期字符串 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentDateString() {
		return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
	}
	/**
	 * 返回当前日期字符串 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getNumberString() {
		return dateToString(getCurrentDateTime(), NUMBER_PATTERN);
	}

	/**
	 * 返回当前日期+时间字符串 yyyy-MM-dd hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStringWithTime(Date date) {

		return dateToString(date, DATETIME_PATTERN);
	}

	/**
	 * 返回当前日期+时间字符串 yyyyMMdd
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStringWithDay(Date date) {

		return dateToString(date, ISO_DATE_FORMAT);
	}

	/**
	 * 日期增加-按日增加
	 * 
	 * @param date
	 * @param days
	 * @return java.util.Date
	 */
	public static Date dateIncreaseByDay(Date date, int days) {

		Calendar cal = GregorianCalendar.getInstance(TimeZone
				.getTimeZone("GMT"));
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	/**
	 * 日期增加-按月增加
	 * 
	 * @param date
	 * @param days
	 * @return java.util.Date
	 */
	public static Date dateIncreaseByMonth(Date date, int mnt) {

		Calendar cal = GregorianCalendar.getInstance(TimeZone
				.getTimeZone("GMT"));
		cal.setTime(date);
		cal.add(Calendar.MONTH, mnt);

		return cal.getTime();
	}

	/**
	 * 日期增加-按年增加
	 * 
	 * @param date
	 * @param mnt
	 * @return java.util.Date
	 */
	public static Date dateIncreaseByYear(Date date, int mnt) {

		Calendar cal = GregorianCalendar.getInstance(TimeZone
				.getTimeZone("GMT"));
		cal.setTime(date);
		cal.add(Calendar.YEAR, mnt);

		return cal.getTime();
	}

	/**
	 * 日期增加
	 * 
	 * @param date
	 *            日期字符串 yyyy-MM-dd
	 * @param days
	 * @return 日期字符串 yyyy-MM-dd
	 */
	public static String dateIncreaseByDay(String date, int days) {
		return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
	}

	/**
	 * 日期增加
	 * 
	 * @param date
	 *            日期字符串
	 * @param fmt
	 *            日期格式
	 * @param days
	 * @return
	 */
	public static String dateIncreaseByDay(String date, String fmt, int days) {
		return dateIncrease(date, fmt, Calendar.DATE, days);
	}

	/**
	 * 日期字符串格式转换
	 * 
	 * @param src
	 *            日期字符串
	 * @param srcfmt
	 *            源日期格式
	 * @param desfmt
	 *            目标日期格式
	 * @return
	 */
	public static String stringToString(String src, String srcfmt, String desfmt) {
		return dateToString(stringToDate(src, srcfmt), desfmt);
	}

	/**
	 * 取得当前日期是星期几 按前台的习惯,一周从周一开始
	 * 
	 * @return
	 */
	public static int getCurrentWeekDay() {
		Date date = getCurrentDateTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int[] defineWeek = new int[] { 7, 1, 2, 3, 4, 5, 6 };
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		return defineWeek[weekDay - 1];
	}

	/**
	 * 取得本周星期几的日期
	 * 
	 * @param date
	 * @param weekIndex
	 *            一周从周一开始
	 * @return
	 */
	public static String getWeekday(String strDate, int weekIndex, String fmt) {
		if (StringUtil.isNullOrEmpty(strDate) || weekIndex < 1 || weekIndex > 7)
			return "";
		if (StringUtil.isNullOrEmpty(fmt))
			fmt = ISO_EXPANDED_DATE_FORMAT;
		if (weekIndex == 7)
			strDate = dateIncreaseByDay(strDate, fmt, 7);
		Date date = stringToDate(strDate);
		Calendar c = Calendar.getInstance();
		int[] weeks = new int[] { Calendar.MONDAY, Calendar.TUESDAY,
				Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
				Calendar.SATURDAY, Calendar.SUNDAY, };
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, weeks[weekIndex - 1]);
		return new SimpleDateFormat(fmt).format(c.getTime());
	}

	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);// 年
	}

	public static Date addMinuteToDate(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minute);
		return cal.getTime();
	}
	public static Date addSECONDToDate(Date date, int SECOND) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, SECOND);
		return cal.getTime();
	}
	public static Date getDateParameter(String data, Date defaultValue,
			int dateType) {
		Pattern p1 = Pattern.compile("\\d+-\\d+-\\d+");
		Pattern p2 = Pattern.compile("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+");
		String param = data;
		Date rtnDate = defaultValue;
		if (param != null) {
			if (p1.matcher(param).matches()) {
				rtnDate = parseDates(param);
			} else if (p2.matcher(param).matches()) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				try {
					rtnDate = sdf.parse(param);
				} catch (ParseException e) {
					return defaultValue;
				}
			}
		}
		if (rtnDate == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(rtnDate);
		switch (dateType) {
		case START_OF_DATE:
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			break;
		case END_OF_DATE:
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			break;
		case ANYTIME_OF_DATE:
			break;
		}
		return c.getTime();
	}

	public static Date parseDates(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getFullDateWeekTime(Date date) {
		try {
			String formater = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat format = new SimpleDateFormat(formater);
			// Date date = format.parse(sDate);
			format.applyPattern("yyyy-MM-dd HH:mm:ss E");
			return format.format(date);
		} catch (Exception ex) {
			return "";
		}
	}

	public static String getWeek(Date date) {
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
				"星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0)
			dayOfWeek = 0;
		return dayNames[dayOfWeek];
	}

	public static Date getTargeTime(Date date, Integer changeDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, changeDay);
		return c.getTime();
	}

	public static String formatDuring(long mss) {
		long hours = (mss / (1000 * 60 * 60));
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		String hoursStr=String.valueOf(hours);
		if(hoursStr.length()==1){
			hoursStr="0"+hoursStr;
		}
		String minutesStr=String.valueOf(minutes);
		if(minutesStr.length()==1){
			minutesStr="0"+minutesStr;
		}
		String secondsStr=String.valueOf(seconds);
		if(secondsStr.length()==1){
			secondsStr="0"+secondsStr;
		}
		return MessageFormat.format("{0}:{1}:{2}", hoursStr,minutesStr,secondsStr);
	}
	public static Date getTodatFirstTime(){
		long current=System.currentTimeMillis();//当前时间毫秒数
		long zero=current/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
		return new Date(zero);//今天零点零分零秒
	}
	public static Date getDayFirstTime(Date date) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}


	public static Date getDayLastTime(Date date) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTime(date);
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}
	public static Date addDate(Date date, Integer dateNum) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE, dateNum);
		return cl.getTime();
	}
	public static void main(String[] args) {
		Date date = getTodatFirstTime();
		System.out.println(dateToString(date, DATETIME_PATTERN));
	}

}
