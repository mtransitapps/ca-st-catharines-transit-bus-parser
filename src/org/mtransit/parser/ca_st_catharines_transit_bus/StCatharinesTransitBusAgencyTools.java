package org.mtransit.parser.ca_st_catharines_transit_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirectionType;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

// http://www.niagararegion.ca/government/opendata/data-set.aspx#id=32
// http://maps-dev.niagararegion.ca/GoogleTransit/NiagaraRegionTransit.zip
public class StCatharinesTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-st-catharines-transit-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new StCatharinesTransitBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating St Catharines Transit bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this, true);
		super.start(args);
		System.out.printf("\nGenerating St Catharines Transit bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}


	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	private static final String ST_CATHARINES_TRANSIT_COMMISSION = "St. Catharines Transit Commission";

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (!gRoute.getAgencyId().contains(ST_CATHARINES_TRANSIT_COMMISSION)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeStop(GStop gStop) {
		if (IGNORE_STOP_ID.matcher(gStop.getStopId()).find()) {
			return true; // other agency
		}
		return super.excludeStop(gStop);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // using route short name as route ID
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.removePoints(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "008E1A"; // GREEN (from web site CSS)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_ED008C = "ED008C";
	private static final String COLOR_F68713 = "F68713";
	private static final String COLOR_4CC6F5 = "4CC6F5";
	private static final String COLOR_0A8ED8 = "0A8ED8";
	private static final String COLOR_486762 = "486762";
	private static final String COLOR_F25373 = "F25373";
	private static final String COLOR_8E1890 = "8E1890";
	private static final String COLOR_00ADEF = "00ADEF";
	private static final String COLOR_00A650 = "00A650";
	private static final String COLOR_24528E = "24528E";
	private static final String COLOR_166FC1 = "166FC1";
	private static final String COLOR_C81070 = "C81070";
	private static final String COLOR_ED1B24 = "ED1B24";
	private static final String COLOR_A4835A = "A4835A";
	private static final String COLOR_00823C = "00823C";
	private static final String COLOR_485683 = "485683";
	private static final String COLOR_48A1AF = "48A1AF";
	private static final String COLOR_4594A9 = "4594A9";
	private static final String COLOR_92D050 = "92D050";
	private static final String COLOR_E77B48 = "E77B48";
	private static final String COLOR_005496 = "005496";
	private static final String COLOR_00B050 = "00B050";

	@Override
	public String getRouteColor(GRoute gRoute) {
		int rsn = Integer.parseInt(gRoute.getRouteShortName());
		switch (rsn) {
		// @formatter:off
		case 1: return COLOR_ED1B24;
		case 2: return COLOR_00A650;
		case 3: return COLOR_ED008C;
		case 4: return COLOR_F68713;
		case 5: return COLOR_8E1890;
		case 6: return COLOR_ED1B24;
		case 7: return COLOR_4CC6F5;
		case 8: return COLOR_48A1AF;
		case 9: return COLOR_48A1AF;
		case 10: return COLOR_24528E;
		case 11: return COLOR_0A8ED8;
		case 12: return COLOR_00A650;
		case 14: return COLOR_C81070;
		case 15: return COLOR_00823C;
		case 16: return COLOR_ED1B24;
		case 17: return COLOR_8E1890;
		case 18: return COLOR_00823C;
		case 20: return COLOR_485683;
		case 21: return COLOR_486762;
		case 22: return COLOR_F25373;
		case 23: return COLOR_8E1890;
		case 25: return COLOR_ED1B24;
		case 26: return COLOR_ED1B24;
		case 27: return COLOR_ED1B24;
		case 28: return COLOR_92D050;
		case 29: return COLOR_4594A9;
		case 30: return COLOR_005496;
		case 31: return COLOR_00B050;
		case 32: return COLOR_166FC1;
		case 33: return COLOR_166FC1;
		case 34: return null; // TODO ?
		case 101: return COLOR_ED1B24;
		case 102: return COLOR_166FC1;
		case 104: return COLOR_00ADEF;
		case 106: return COLOR_ED1B24;
		case 108: return COLOR_00A650;
		case 109: return COLOR_A4835A;
		case 110: return COLOR_24528E;
		case 112: return COLOR_166FC1;
		case 114: return COLOR_C81070;
		case 115: return COLOR_00823C;
		case 116: return COLOR_ED1B24;
		case 117: return COLOR_A4835A;
		case 118: return COLOR_00823C;
		case 120: return COLOR_485683;
		case 122: return COLOR_48A1AF;
		case 124: return COLOR_E77B48;
		case 128: return COLOR_ED1B24;
		case 216: return null; // TODO ?
		// @formatter:on
		default:
			System.out.printf("\nUnexpected route color for %s!\n", gRoute);
			System.exit(-1);
			return null;
		}
	}

	private static final String LINWELL_RD = "Linwell Rd";
	private static final String NC_NOTL_CAMPUS = "NC NOTL Campus";
	private static final String DOWNTOWN = "Downtown";
	private static final String THOROLD = "Thorold";
	private static final String THOROLD_SOUTH = THOROLD + " South";
	private static final String PORT_ROBINSON = "Port Robinson";
	private static final String BROCK_UNIVERSITY = "Brock"; // University
	private static final String PEN_CTR = "Pen Ctr";
	private static final String DUNKELD_CARLTON = "Dunkeld & Carlton";
	private static final String FAIRVIEW_MALL = "Fairview Mall";
	private static final String WEST_ = "West";

	private static final String STOP_ = "STC_S_2016_";
	private static final String STOP_0218 = STOP_ + "Stop0218";
	private static final String STOP_0228 = STOP_ + "Stop0228";
	private static final String STOP_0470 = STOP_ + "Stop0470";
	private static final String STOP_0632 = STOP_ + "Stop0632";
	private static final String STOP_0710 = STOP_ + "Stop0710";
	private static final String STOP_0778 = STOP_ + "Stop0778";
	private static final String STOP_0839 = STOP_ + "Stop0839";
	private static final String STOP_0997 = STOP_ + "Stop0997";
	private static final String STOP_1206 = STOP_ + "Stop1206";
	private static final String STOP_1290 = STOP_ + "Stop1290";
	private static final String STOP_2206 = STOP_ + "Stop2206";
	private static final String STOP_ALNBG_LYN = STOP_ + "AlnbgLyn";
	private static final String STOP_BAS = STOP_ + "BAS";
	private static final String STOP_BIS = STOP_ + "BIS";
	private static final String STOP_BRU = STOP_ + "BRU";
	private static final String STOP_CRMT_TOWP = STOP_ + "CrmtTowp";
	private static final String STOP_DNKL_CRLT = STOP_ + "DnklCrlt";
	private static final String STOP_DTT = STOP_ + "DTT";
	private static final String STOP_FVM = STOP_ + "FVM";
	private static final String STOP_GLND_CMPS = STOP_ + "GlndCmps";
	private static final String STOP_GRDG_GLMR = STOP_ + "GrdgGlmr";
	private static final String STOP_MAC_T_LOUT = STOP_ + "MacTLout";
	private static final String STOP_NI_FLS_ALL = STOP_ + "NiFlsAll";
	private static final String STOP_ORMD_RICH = STOP_ + "OrmdRich";
	private static final String STOP_PELM_GNDL = STOP_ + "PelmGndl";
	private static final String STOP_PEN_CNTR = STOP_ + "Pen Cntr";
	private static final String STOP_TWNL_QUEN = STOP_ + "TwnlQuen";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(14l, new RouteTripSpec(14l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, DUNKELD_CARLTON, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, FAIRVIEW_MALL) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { STOP_FVM, STOP_0710, STOP_DNKL_CRLT })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { STOP_DNKL_CRLT, STOP_0470, STOP_FVM })) //
				.compileBothTripSort());
		map2.put(20l, new RouteTripSpec(20l, //
				0, MTrip.HEADSIGN_TYPE_STRING, THOROLD, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_PEN_CNTR, STOP_ORMD_RICH, STOP_CRMT_TOWP })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_CRMT_TOWP, STOP_TWNL_QUEN, STOP_0997, STOP_PEN_CNTR })) //
				.compileBothTripSort());
		map2.put(22l, new RouteTripSpec(22l, //
				0, MTrip.HEADSIGN_TYPE_STRING, PORT_ROBINSON, // LYNN_CR
				1, MTrip.HEADSIGN_TYPE_STRING, THOROLD_SOUTH) // TOWPATH
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_CRMT_TOWP, STOP_BAS, STOP_NI_FLS_ALL, STOP_2206, STOP_ALNBG_LYN, STOP_BIS })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_BIS, STOP_ALNBG_LYN, STOP_NI_FLS_ALL, STOP_BAS, STOP_CRMT_TOWP })) //
				.compileBothTripSort());
		map2.put(23l, new RouteTripSpec(23l, //
				0, MTrip.HEADSIGN_TYPE_STRING, WEST_, //
				1, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY) //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_BRU, STOP_0839, STOP_MAC_T_LOUT })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_MAC_T_LOUT, STOP_0778, STOP_PELM_GNDL, STOP_BRU })) //
				.compileBothTripSort());
		map2.put(25l, new RouteTripSpec(25l, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY, //
				1, MTrip.HEADSIGN_TYPE_STRING, DOWNTOWN) //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_DTT, STOP_0228, STOP_BRU })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_BRU, STOP_1206, STOP_DTT })) //
				.compileBothTripSort());
		map2.put(34l, new RouteTripSpec(34l, //
				0, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR, //
				1, MTrip.HEADSIGN_TYPE_STRING, NC_NOTL_CAMPUS) //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_GLND_CMPS, STOP_0218, STOP_PEN_CNTR })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_PEN_CNTR, STOP_0632, STOP_GLND_CMPS })) //
				.compileBothTripSort());
		map2.put(120l, new RouteTripSpec(120l, //
				0, MTrip.HEADSIGN_TYPE_STRING, THOROLD, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_PEN_CNTR, STOP_ORMD_RICH, STOP_CRMT_TOWP })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { STOP_CRMT_TOWP, STOP_1290, STOP_PEN_CNTR })) //
				.compileBothTripSort());
		map2.put(216l, new RouteTripSpec(216l, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY, //
				1, MTrip.HEADSIGN_TYPE_STRING, "") //
				.addTripSort(0, //
						Arrays.asList(new String[] { STOP_DTT, STOP_GRDG_GLMR, STOP_BRU })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { /* no stops */})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()));
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		if (mRoute.getId() == 122l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(PEN_CTR, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(BROCK_UNIVERSITY, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.getId() == 124l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(BROCK_UNIVERSITY, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(PEN_CTR, gTrip.getDirectionId());
				return;
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (mTrip.getRouteId() == 3l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 5l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(LINWELL_RD, mTrip.getHeadsignId());
				return true;
			} else if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 101l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 102l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 112l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexptected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern STARTS_WITH_RSN_RLN = Pattern.compile("(^[0-9]{1,3} (([\\w]+[\\.]? )+\\- )*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern CENTR = Pattern.compile("((^|\\W){1}(cent[r]?)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String CENTR_REPLACEMENT = "$2Center$4";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = STARTS_WITH_RSN_RLN.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CENTR.matcher(tripHeadsign).replaceAll(CENTR_REPLACEMENT);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern AND_NOT = Pattern.compile("(&)", Pattern.CASE_INSENSITIVE);
	private static final String AND_NOT_REPLACEMENT = "and";

	private static final Pattern AND = Pattern.compile("((^|\\W){1}(and)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String AND_REPLACEMENT = "$2&$4";

	private static final Pattern AT = Pattern.compile(
			"((^|\\W){1}(across fr[\\.]?|after|at|before|between both|between|east of|in front of|north of|opp|south of|west of)(\\W|$){1})",
			Pattern.CASE_INSENSITIVE);
	private static final String AT_REPLACEMENT = "$2/$4";

	private static final Pattern AND_SLASH = Pattern.compile("((^|\\W){1}(&)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String AND_SLASH_REPLACEMENT = "$2/$4";

	private static final Pattern ENDS_WITH = Pattern.compile("((&|/|\\-)[\\W]*$)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = gStopName.toLowerCase(Locale.ENGLISH);
		gStopName = AND_NOT.matcher(gStopName).replaceAll(AND_NOT_REPLACEMENT); // fix Alex&ra
		gStopName = AND.matcher(gStopName).replaceAll(AND_REPLACEMENT);
		gStopName = AND_SLASH.matcher(gStopName).replaceAll(AND_SLASH_REPLACEMENT);
		gStopName = AT.matcher(gStopName).replaceAll(AT_REPLACEMENT);
		gStopName = CleanUtils.removePoints(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = ENDS_WITH.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		return CleanUtils.cleanLabel(gStopName);
	}

	private static final String ZERO_0 = "0";

	private static final String STO = "Sto";
	private static final String STC_F_STOP = "STC_F_Stop";
	private static final String STC_F2015_STOP = "STC_F2015_Stop";
	private static final String STC_F2015_STO = "STC_F2015_Sto";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO_0.equals(gStop.getStopCode())) {
			if (gStop.getStopId().startsWith(STC_F_STOP)) {
				return gStop.getStopId().substring(STC_F_STOP.length());
			}
			if (gStop.getStopId().startsWith(STC_F2015_STOP)) {
				return gStop.getStopId().substring(STC_F2015_STOP.length());
			}
			if (gStop.getStopId().startsWith(STC_F2015_STO)) {
				return gStop.getStopId().substring(STC_F2015_STO.length());
			}
			return null;
		}
		if (gStop.getStopCode().startsWith(STO)) {
			return gStop.getStopCode().substring(STO.length());
		}
		if (!Utils.isDigitsOnly(gStop.getStopCode())) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	private static final Pattern PRE_STOP_ID = Pattern.compile("(" //
			+ "STC_[F|S|W][0-9]{4}_Stop|" //
			+ "STC_[F|S|W][0-9]{4}Stop|" //
			+ "STC_[F|S|W][0-9]{4}_|" //
			+ "STC_[F|S|W][0-9]{4}|" //
			+ "STC_[F|S|W]Stop|" //
			+ "STC_[F|S|W]_Stop|" //
			+ "STC_[F|S|W]_|" //
			+ "STC_[F|S|W]|" //
			+ "Sto" //
			+ ")", //
			Pattern.CASE_INSENSITIVE);

	private static final Pattern IGNORE_STOP_ID = Pattern.compile("(^(S_FE|NF|PC|WE))", Pattern.CASE_INSENSITIVE);

	private static final String ABBY = "Abby";
	private static final String ALL = "All";
	private static final String ALNBG = "Alnbg";
	private static final String BRCK = "Brck";
	private static final String CLRK = "Clrk";
	private static final String CMGS = "Cmgs";
	private static final String CNFD = "Cnfd";
	private static final String CRMT = "Crmt";
	private static final String DNKL = "Dnkl";
	private static final String DNTN = "Dntn";
	private static final String FARV = "Farv";
	private static final String FRTH = "Frth";
	private static final String GEN = "Gen";
	private static final String GLND = "Glnd";
	private static final String GRDG = "Grdg";
	private static final String HAIG = "Haig";
	private static final String HRTZ = "Hrtz";
	private static final String KEFR = "Kefr";
	private static final String LOCK = "Lock";
	private static final String LSHR = "Lshr";
	private static final String MAC_T = "MacT";
	private static final String NI_FLS = "NiFls";
	private static final String NW_GN = "NwGn";
	private static final String ONTARIO_ST = "Ontario St";
	private static final String ORMD = "Ormd";
	private static final String PEN = "Pen";
	private static final String QRVW = "Qrvw";
	private static final String RKWD = "Rkwd";
	private static final String SCMN = "Scmn";
	private static final String SCOT = "Scot";
	private static final String SRNG = "Srng";
	private static final String ST_P = "StP";
	private static final String ST_PW = "StPW";
	private static final String SULV = "Sulv";
	private static final String WAL = "Wal";
	private static final String WCTR = "Wctr";
	private static final String WEST = "West";
	private static final String WLDW = "Wldw";
	private static final String ARTH = "Arth";
	private static final String BCHN = "Bchn";
	private static final String BNTG = "Bntg";
	private static final String BRHL = "Brhl";
	private static final String CAMP = "Camp";
	private static final String CHUR = "Chur";
	private static final String CLR = "Clr";
	private static final String CMPS = "Cmps";
	private static final String CNTR = "Cntr";
	private static final String COLL = "Coll";
	private static final String COLR = "Colr";
	private static final String CONF = "Conf";
	private static final String CRLT = "Crlt";
	private static final String CUGA = "Cuga";
	private static final String ECHR = "Echr";
	private static final String FACR = "Facr";
	private static final String GENV = "Genv";
	private static final String GLMR = "Glmr";
	private static final String GLNR = "Glnr";
	private static final String GNDL = "Gndl";
	private static final String GRNT = "Grnt";
	private static final String HOSP = "Hosp";
	private static final String HP = "Hp";
	private static final String LAKE = "Lake";
	private static final String LINW = "Linw";
	private static final String LOUT = "Lout";
	private static final String LNHVN = "Lnhvn";
	private static final String LYN = "Lyn";
	private static final String MAIN = "Main";
	private static final String MALL = "Mall";
	private static final String MART = "Mart";
	private static final String MC_TB = "McTb";
	private static final String MERT = "Mert";
	private static final String MRDL = "Mrdl";
	private static final String MRTV = "Mrtv";
	private static final String NIAG = "Niag";
	private static final String OAKD = "Oakd";
	private static final String ONT = "Ont";
	private static final String PARK = "Park";
	private static final String PELM = "Pelm";
	private static final String QUEN = "Quen";
	private static final String QUNS = "Quns";
	private static final String RES = "Res";
	private static final String RICH = "Rich";
	private static final String ST_D = "StD";
	private static final String TERM = "Term";
	private static final String TOWP = "Towp";
	private static final String TWNL = "Twnl";
	private static final String TUPP = "Tupp";
	private static final String UNIV = "Univ";
	private static final String VINE = "Vine";
	private static final String VSKL = "Vskl";
	private static final String WDRW = "Wdrw";
	private static final String WLND = "Wlnd";
	private static final String WMBL = "Wmbl";

	private static final String CD = "CD";
	private static final String CRL = "CRL";
	private static final String GLI = "GLI";
	private static final String LKV = "LKV";
	private static final String LLI = "LLI";
	private static final String NOTL = "NOTL";
	private static final String PGL = "PGL";
	private static final String SCWE = "SCWE";

	private static final String WEL = "WEL";
	private static final String SWM = "SWM";
	private static final String PEN2 = "PEN";
	private static final String NFT = "NFT";
	private static final String DTT = "DTT";
	private static final String BAS = "BAS";
	private static final String BRU = "BRU";
	private static final String DAS = "DAS";
	private static final String FVM = "FVM";
	private static final String GLW = "GLW";
	private static final String LIG = "LIG";
	private static final String MIW = "MIW";
	private static final String QUP = "QUP";
	private static final String WSM = "WSM";

	@Override
	public int getStopId(GStop gStop) {
		if (IGNORE_STOP_ID.matcher(gStop.getStopId()).find()) {
			return -1; // other agency
		}
		String stopCode = gStop.getStopCode();
		if (stopCode == null || stopCode.length() == 0 || ZERO_0.equals(stopCode)) {
			stopCode = gStop.getStopId();
		}
		stopCode = PRE_STOP_ID.matcher(stopCode).replaceAll(StringUtils.EMPTY);
		if (Utils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		if (stopCode.equals(DTT)) {
			return 100000;
		} else if (stopCode.equals(NFT)) {
			return 100001;
		} else if (stopCode.equals(PEN2)) {
			return 100002;
		} else if (stopCode.equals(SWM)) {
			return 100003;
		} else if (stopCode.equals(WEL)) {
			return 100004;
		} else if (stopCode.equals(BAS)) {
			return 100005;
		} else if (stopCode.equals(BRU)) {
			return 100006;
		} else if (stopCode.equals(DAS)) {
			return 100007;
		} else if (stopCode.equals(FVM)) {
			return 100008;
		} else if (stopCode.equals(GLW)) {
			return 100009;
		} else if (stopCode.equals(LIG)) {
			return 100010;
		} else if (stopCode.equals(QUP)) {
			return 100011;
		} else if (stopCode.equals(WSM)) {
			return 100012;
		} else if (stopCode.equals(MIW)) {
			return 100013;
		} else if (stopCode.equals("BIS")) {
			return 100014;
		} else if (stopCode.equals("BRR")) {
			return 100015;
		} else if (stopCode.equals("CER")) {
			return 100016;
		}
		try {
			Matcher matcher = DIGITS.matcher(stopCode);
			if (matcher.find()) {
				int routeId = Integer.parseInt(matcher.group());
				if (stopCode.startsWith(CD)) {
					routeId += 30000;
				} else if (stopCode.startsWith(CRL)) {
					routeId += 40000;
				} else if (stopCode.startsWith(GLI)) {
					routeId += 70000;
				} else if (stopCode.startsWith(LKV)) {
					routeId += 120000;
				} else if (stopCode.startsWith(LLI)) {
					routeId += 130000;
				} else if (stopCode.startsWith(NOTL)) {
					routeId += 140000;
				} else if (stopCode.startsWith(PGL)) {
					routeId += 160000;
				} else if (stopCode.startsWith(SCWE)) {
					routeId += 190000;
				} else {
					System.out.printf("\nUnexpected stop ID (starts with digits) %s", gStop);
					System.exit(-1);
					routeId = -1;
				}
				return routeId;
			}
		} catch (Exception e) {
			System.out.printf("\nError while finding stop ID for %s", gStop);
			e.printStackTrace();
		}
		int routeId;
		if (stopCode.startsWith(ALNBG)) {
			routeId = 100000;
		} else if (stopCode.startsWith(ARTH)) {
			routeId = 110000;
		} else if (stopCode.startsWith(BNTG)) {
			routeId = 200000;
		} else if (stopCode.startsWith(BRCK)) {
			routeId = 210000;
		} else if (stopCode.startsWith(CLRK)) {
			routeId = 300000;
		} else if (stopCode.startsWith(CMGS)) {
			routeId = 310000;
		} else if (stopCode.startsWith(CNFD)) {
			routeId = 320000;
		} else if (stopCode.startsWith(CRLT)) {
			routeId = 330000;
		} else if (stopCode.startsWith(CRMT)) {
			routeId = 340000;
		} else if (stopCode.startsWith(DNKL)) {
			routeId = 400000;
		} else if (stopCode.startsWith(DNTN)) {
			routeId = 410000;
		} else if (stopCode.startsWith(FARV)) {
			routeId = 600000;
		} else if (stopCode.startsWith(FRTH)) {
			routeId = 610000;
		} else if (stopCode.startsWith(GEN)) {
			routeId = 700000;
		} else if (stopCode.startsWith(GENV)) {
			routeId = 710000;
		} else if (stopCode.startsWith(GLND)) {
			routeId = 720000;
		} else if (stopCode.startsWith(GNDL)) {
			routeId = 7300000;
		} else if (stopCode.startsWith(GRDG)) {
			routeId = 740000;
		} else if (stopCode.startsWith(GRNT)) {
			routeId = 750000;
		} else if (stopCode.startsWith(HAIG)) {
			routeId = 800000;
		} else if (stopCode.startsWith(HRTZ)) {
			routeId = 810000;
		} else if (stopCode.startsWith(KEFR)) {
			routeId = 1100000;
		} else if (stopCode.startsWith(LAKE)) {
			routeId = 1200000;
		} else if (stopCode.startsWith(LOCK)) {
			routeId = 1210000;
		} else if (stopCode.startsWith(LSHR)) {
			routeId = 1220000;
		} else if (stopCode.startsWith(MAC_T)) {
			routeId = 1300000;
		} else if (stopCode.startsWith(MERT)) {
			routeId = 1310000;
		} else if (stopCode.startsWith(MRDL)) {
			routeId = 1320000;
		} else if (stopCode.startsWith(NIAG)) {
			routeId = 1400000;
		} else if (stopCode.startsWith(NI_FLS)) {
			routeId = 1410000;
		} else if (stopCode.startsWith(NW_GN)) {
			routeId = 1420000;
		} else if (stopCode.startsWith(ONT) || gStop.getStopName().startsWith(ONTARIO_ST)) {
			routeId = 1500000;
		} else if (stopCode.startsWith(ORMD)) {
			routeId = 1510000;
		} else if (stopCode.startsWith(PELM)) {
			routeId = 1600000;
		} else if (stopCode.startsWith(PEN)) {
			routeId = 1610000;
		} else if (stopCode.startsWith(QRVW)) {
			routeId = 1700000;
		} else if (stopCode.startsWith(RICH)) {
			routeId = 1800000;
		} else if (stopCode.startsWith(RKWD)) {
			routeId = 1810000;
		} else if (stopCode.startsWith(SCMN)) {
			routeId = 1900000;
		} else if (stopCode.startsWith(SCOT)) {
			routeId = 1910000;
		} else if (stopCode.startsWith(SRNG)) {
			routeId = 1920000;
		} else if (stopCode.startsWith(ST_D)) {
			routeId = 1930000;
		} else if (stopCode.startsWith(ST_P)) {
			routeId = 1940000;
		} else if (stopCode.startsWith(ST_PW)) {
			routeId = 1950000;
		} else if (stopCode.startsWith(SULV)) {
			routeId = 1960000;
		} else if (stopCode.startsWith(TWNL)) {
			routeId = 2000000;
		} else if (stopCode.startsWith(VINE)) {
			routeId = 2200000;
		} else if (stopCode.startsWith(VSKL)) {
			routeId = 2210000;
		} else if (stopCode.startsWith(WAL)) {
			routeId = 2300000;
		} else if (stopCode.startsWith(WCTR)) {
			routeId = 2310000;
		} else if (stopCode.startsWith(WEST)) {
			routeId = 2320000;
		} else if (stopCode.startsWith(WLDW)) {
			routeId = 2330000;
		} else if (stopCode.startsWith(WLND)) {
			routeId = 2340000;
		} else {
			System.out.printf("\nUnexpected stop ID (starts with) %s", gStop);
			System.exit(-1);
			routeId = -1;
		}
		if (stopCode.endsWith(ABBY)) {
			routeId += 100;
		} else if (stopCode.endsWith(ALL)) {
			routeId += 101;
		} else if (stopCode.endsWith(ARTH)) {
			routeId += 102;
		} else if (stopCode.endsWith(BCHN)) {
			routeId += 200;
		} else if (stopCode.endsWith(BNTG)) {
			routeId += 201;
		} else if (stopCode.endsWith(BRHL)) {
			routeId += 202;
		} else if (stopCode.endsWith(CAMP)) {
			routeId += 300;
		} else if (stopCode.endsWith(CHUR)) {
			routeId += 301;
		} else if (stopCode.endsWith(CLR)) {
			routeId += 302;
		} else if (stopCode.endsWith(CMPS)) {
			routeId += 303;
		} else if (stopCode.endsWith(CNTR)) {
			routeId += 304;
		} else if (stopCode.endsWith(COLL)) {
			routeId += 305;
		} else if (stopCode.endsWith(COLR)) {
			routeId += 306;
		} else if (stopCode.endsWith(CONF)) {
			routeId += 307;
		} else if (stopCode.endsWith(CRLT)) {
			routeId += 308;
		} else if (stopCode.endsWith(CUGA)) {
			routeId += 309;
		} else if (stopCode.endsWith(ECHR)) {
			routeId += 500;
		} else if (stopCode.endsWith(FACR)) {
			routeId += 600;
		} else if (stopCode.endsWith(GENV)) {
			routeId += 700;
		} else if (stopCode.endsWith(GLMR)) {
			routeId += 701;
		} else if (stopCode.endsWith(GLNR)) {
			routeId += 702;
		} else if (stopCode.endsWith(GNDL)) {
			routeId += 703;
		} else if (stopCode.endsWith(GRNT)) {
			routeId += 704;
		} else if (stopCode.endsWith(HOSP)) {
			routeId += 800;
		} else if (stopCode.endsWith(HP)) {
			routeId += 801;
		} else if (stopCode.endsWith(LAKE)) {
			routeId += 1200;
		} else if (stopCode.endsWith(LINW)) {
			routeId += 1201;
		} else if (stopCode.endsWith(LOUT)) {
			routeId += 1203;
		} else if (stopCode.endsWith(LNHVN)) {
			routeId += 1204;
		} else if (stopCode.endsWith(LYN)) {
			routeId += 1205;
		} else if (stopCode.endsWith(MAIN)) {
			routeId += 1300;
		} else if (stopCode.endsWith(MALL)) {
			routeId += 1301;
		} else if (stopCode.endsWith(MART)) {
			routeId += 1302;
		} else if (stopCode.endsWith(MC_TB)) {
			routeId += 1303;
		} else if (stopCode.endsWith(MERT)) {
			routeId += 1304;
		} else if (stopCode.endsWith(MRDL)) {
			routeId += 1305;
		} else if (stopCode.endsWith(MRTV)) {
			routeId += 1306;
		} else if (stopCode.endsWith(NIAG)) {
			routeId += 1400;
		} else if (stopCode.endsWith(OAKD)) {
			routeId += 1500;
		} else if (stopCode.endsWith(ONT)) {
			routeId += 1501;
		} else if (stopCode.endsWith(PARK)) {
			routeId += 1600;
		} else if (stopCode.endsWith(PELM)) {
			routeId += 1601;
		} else if (stopCode.endsWith(QUEN)) {
			routeId += 1700;
		} else if (stopCode.endsWith(QUNS)) {
			routeId += 1701;
		} else if (stopCode.endsWith(RES)) {
			routeId += 1800;
		} else if (stopCode.endsWith(RICH)) {
			routeId += 1801;
		} else if (stopCode.endsWith(ST_D)) {
			routeId += 1900;
		} else if (stopCode.endsWith(TERM)) {
			routeId += 2000;
		} else if (stopCode.endsWith(TOWP)) {
			routeId += 2001;
		} else if (stopCode.endsWith(TWNL)) {
			routeId += 2002;
		} else if (stopCode.endsWith(TUPP)) {
			routeId += 2003;
		} else if (stopCode.endsWith(UNIV)) {
			routeId += 2100;
		} else if (stopCode.endsWith(VINE)) {
			routeId += 2200;
		} else if (stopCode.endsWith(VSKL)) {
			routeId += 2201;
		} else if (stopCode.endsWith(WDRW)) {
			routeId += 2300;
		} else if (stopCode.endsWith(WLND)) {
			routeId += 2301;
		} else if (stopCode.endsWith(WMBL)) {
			routeId += 2302;
		} else {
			System.out.printf("\nUnexpected stop ID (ends with) %s", gStop);
			System.exit(-1);
			routeId = -1;
		}
		return routeId;
	}
}
