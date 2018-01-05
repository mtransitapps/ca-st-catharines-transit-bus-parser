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
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

// http://www.niagararegion.ca/government/opendata/data-set.aspx#id=32
// https://maps.niagararegio n.ca/googletransit/NiagaraRegionTransit.zip
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
		if (gRoute.getRouteLongName().startsWith("IMT - ")) {
			return true; // Niagara Region Transit
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
	private static final String COLOR_00A650 = "00A650";
	private static final String COLOR_24528E = "24528E";
	private static final String COLOR_166FC1 = "166FC1";
	private static final String COLOR_C81070 = "C81070";
	private static final String COLOR_ED1B24 = "ED1B24";
	private static final String COLOR_00823C = "00823C";
	private static final String COLOR_485683 = "485683";
	private static final String COLOR_48A1AF = "48A1AF";
	private static final String COLOR_3A9CB9 = "3A9CB9";
	private static final String COLOR_92D050 = "92D050";
	private static final String COLOR_00A551 = "00A551";
	private static final String COLOR_005FAC = "005FAC";
	private static final String COLOR_E24E26 = "E24E26";
	private static final String COLOR_4CA392 = "4CA392";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			int rsn = Integer.parseInt(gRoute.getRouteShortName());
			switch (rsn) {
			// @formatter:off
			case 26: return "ED1B24";
			case 27: return "ED1B24";
			case 88: return null; // TODO ?
			case 301: return COLOR_ED1B24;
			case 302: return COLOR_00A650;
			case 303: return COLOR_ED008C;
			case 304: return COLOR_F68713;
			case 305: return COLOR_8E1890;
			case 306: return COLOR_ED1B24;
			case 307: return COLOR_4CC6F5;
			case 308: return COLOR_48A1AF;
			case 309: return COLOR_48A1AF;
			case 310: return COLOR_24528E;
			case 311: return COLOR_0A8ED8;
			case 312: return COLOR_00A650;
			case 314: return COLOR_C81070;
			case 315: return COLOR_00823C;
			case 316: return COLOR_ED1B24;
			case 317: return COLOR_8E1890;
			case 318: return COLOR_00823C;
			case 320: return COLOR_485683;
			case 321: return COLOR_486762;
			case 322: return COLOR_F25373;
			case 323: return COLOR_8E1890;
			case 324: return "0060AD"; // BLUE
			case 325: return COLOR_ED1B24;
			case 326: return COLOR_ED1B24;
			case 327: return COLOR_ED1B24;
			case 328: return COLOR_92D050;
			case 329: return COLOR_3A9CB9;
			case 330: return COLOR_005FAC;
			case 331: return COLOR_00A551;
			case 332: return COLOR_166FC1;
			case 333: return COLOR_166FC1;
			case 335: return COLOR_4CA392;
			case 336: return COLOR_E24E26;
			case 401: return "EE1C25"; // RED
			case 402: return "0072BB"; // BLUE
			case 404: return "00ADEF"; // LIGHT BLUE
			case 406: return "EE1C25"; // RED
			case 408: return "00A652"; // GREEN
			case 409: return "A88B6B"; // LIGHT BROWN
			case 410: return "05558A"; // DRAK BLUE
			case 412: return "0072BB"; // BLUE
			case 414: return "C81c6E"; // PURPLE
			case 415: return "008744"; // GREEN
			case 416: return "EE1C25"; // RED
			case 417: return "A88B6B"; // LIGHT BROWN
			case 418: return "008744"; // GREEN
			case 420: return "485E87"; // BLUE-ISH
			case 421: return "486F6E"; // GREEN-ISH
			case 423: return "7570B3"; // LIGHT PURPLE // ?
			case 424: return "0060AD"; // BLUE
			case 425: return null; // TODO ?
			case 428: return "A3CE62"; // LIGHT GREEN
			case 431: return "00A652"; // GREEN
			case 432: return "ED008E"; // PINK
			case 435: return "4FA491";
			case 436: return "F58345"; // ORANGE
			// @formatter:on
			default:
				System.out.printf("\nUnexpected route color for %s!\n", gRoute);
				System.exit(-1);
				return null;
			}
		}
		return super.getRouteColor(gRoute);
	}

	private static final String DOWNTOWN = "Downtown";
	private static final String THOROLD = "Thorold";
	private static final String THOROLD_SOUTH = THOROLD + " South";
	private static final String PORT_ROBINSON = "Port Robinson";
	private static final String BROCK_UNIVERSITY_SHORT = "Brock"; // University
	private static final String PEN_CTR = "Pen Ctr";
	private static final String FAIRVIEW_MALL = "Fairview Mall";
	private static final String WEST_ = "West";

	private static final String STOP_ = "STC_W18_";
	private static final String STOP_0167 = STOP_ + "Stop0167";
	private static final String STOP_0168 = STOP_ + "Stop0168";
	private static final String STOP_0219 = STOP_ + "Stop0219";
	private static final String STOP_0220 = STOP_ + "Stop0220";
	private static final String STOP_0222 = STOP_ + "Stop0222";
	private static final String STOP_0223 = STOP_ + "Stop0223";
	private static final String STOP_0224 = STOP_ + "Stop0224";
	private static final String STOP_0237 = STOP_ + "Stop0237";
	private static final String STOP_0238 = STOP_ + "Stop0238";
	private static final String STOP_0778 = STOP_ + "Stop0778";
	private static final String STOP_0781 = STOP_ + "Stop0781";
	private static final String STOP_0784 = STOP_ + "Stop0784";
	private static final String STOP_0794 = STOP_ + "Stop0794";
	private static final String STOP_0797 = STOP_ + "Stop0797";
	private static final String STOP_0831 = STOP_ + "Stop0831";
	private static final String STOP_0839 = STOP_ + "Stop0839";
	private static final String STOP_0842 = STOP_ + "Stop0842";
	private static final String STOP_0967 = STOP_ + "Stop0967";
	private static final String STOP_0997 = STOP_ + "Stop0997";
	private static final String STOP_1030 = STOP_ + "Stop1030";
	private static final String STOP_1294 = STOP_ + "Stop1294";
	private static final String STOP_1290 = STOP_ + "Stop1290";
	private static final String STOP_1316 = STOP_ + "Stop1316";
	private static final String STOP_1317 = STOP_ + "Stop1317";
	private static final String STOP_1336 = STOP_ + "Stop1336";
	private static final String STOP_2206 = STOP_ + "Stop2206";

	private static final String STOP_BAS = STOP_ + "BAS";
	private static final String STOP_BRU = STOP_ + "BRU";
	private static final String STOP_RIC = STOP_ + "RIC";

	private static final String STOP_ALNBG_LYN = STOP_ + "AlnbgLyn";
	private static final String STOP_CTO = STOP_ + "CTO";
	private static final String STOP_GLND_GLNR = STOP_ + "GlndGlnr";
	private static final String STOP_MCL = STOP_ + "MCL";
	private static final String STOP_NI_FLS_ALL = STOP_ + "NiFlsAll";
	private static final String STOP_ORMD_RICH = STOP_ + "OrmdRich";
	private static final String STOP_PELM_GNDL = STOP_ + "PelmGndl";
	private static final String STOP_PEN = STOP_ + "PEN";
	private static final String STOP_TLQ = STOP_ + "TLQ";
	private static final String STOP_GNDL_BRHL = STOP_ + "GndlBrhl";
	private static final String STOP_ST_D_CLR = STOP_ + "StDClr";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(320l, new RouteTripSpec(320l, //
				0, MTrip.HEADSIGN_TYPE_STRING, THOROLD, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_PEN, // Pen Centre
								STOP_ORMD_RICH, // ++
								STOP_CTO, // Thorold Towpath Terminal
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_CTO, // Thorold Towpath Terminal
								STOP_TLQ, // Townline Rd & Queen St
								STOP_0997, // ++
								STOP_PEN, // Pen Centre
						})) //
				.compileBothTripSort());
		map2.put(322l, new RouteTripSpec(322l, //
				0, MTrip.HEADSIGN_TYPE_STRING, PORT_ROBINSON, // LYNN_CR
				1, MTrip.HEADSIGN_TYPE_STRING, THOROLD_SOUTH) // TOWPATH
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_CTO, // Thorold Towpath Terminal
								STOP_NI_FLS_ALL, // Niagara Falls Rd & Allanburg Rd
								STOP_2206, // ++
								STOP_ALNBG_LYN, // Allanburg Rd & Lynn Cr
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_ALNBG_LYN, // Allanburg Rd & Lynn Cr
								STOP_BAS, // ++
								STOP_CTO, // Thorold Towpath Terminal
						})) //
				.compileBothTripSort());
		map2.put(323L, new RouteTripSpec(23l, //
				0, MTrip.HEADSIGN_TYPE_STRING, WEST_, //
				1, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_0839, // !=
								STOP_GLND_GLNR, // <>
								STOP_0794, // !=
								STOP_0797, // !=
								STOP_PELM_GNDL, // <>
								STOP_0168, // !=
								STOP_MCL, // MacTurnbull Dr & Louth St
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_MCL, // MacTurnbull Dr & Louth St
								STOP_0778, // ++
								STOP_0167, // !=
								STOP_PELM_GNDL, // <>
								STOP_0781, // !=
								STOP_0784, // !=
								STOP_GLND_GLNR, // <>
								STOP_0831, // !=
								STOP_BRU, // Brock University
						})) //
				.compileBothTripSort());
		map2.put(324L, new RouteTripSpec(324L, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT, //
				1, MTrip.HEADSIGN_TYPE_STRING, "Tupper") //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_1030, // ++
								STOP_RIC, // Richmond St & Confederation Av
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_RIC, // Richmond St & Confederation Av
								STOP_1294, // ++
								STOP_BRU, // Brock University
						})) //
				.compileBothTripSort());
		map2.put(336L, new RouteTripSpec(336L, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_PEN, // Pen Centre
								STOP_0831, // ++
								STOP_BRU, // Brock University
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_0842, //
								STOP_PEN, // Pen Centre
						})) //
				.compileBothTripSort());
		map2.put(420L, new RouteTripSpec(420L, //
				0, MTrip.HEADSIGN_TYPE_STRING, THOROLD, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_PEN, // Pen Centre
								STOP_1336, // != Pen Centre & East Entrance
								STOP_1316, // != Glengarry Rd & Glenhurst Cir
								STOP_0222, // != Glendale Av & Burleigh Hill Dr
								STOP_GNDL_BRHL, // == <> Glendale Av & Burleigh Hill Dr
								STOP_0223, // != 0,Burleigh Hill Dr & Warkdale Dr
								STOP_0224, // != Burleigh Hill Dr & Dalecrest Av
								STOP_ST_D_CLR, // <> St Davids Rd & Collier Rd
								STOP_0967, // != Collier Rd & Broderick Av
								STOP_ORMD_RICH, // Ormond St S. & Richmond St
								STOP_CTO, // Thorold Towpath Terminal
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_CTO, // Thorold Towpath Terminal
								STOP_1290, // != Townline Rd W & Queen St N
								STOP_0997, // != St Davids Rd & Burleigh Hill
								STOP_ST_D_CLR, // <> St Davids Rd & Collier Rd
								STOP_0237, // != Burleigh Hill Dr & Dalecrest Av
								STOP_0238, // != Burleigh Hill School
								STOP_GNDL_BRHL, // == <> Glendale Av & Burleigh Hill Dr
								STOP_0219, // != Glendale Av & Burleigh Hill Dr
								STOP_1317, // != Glengarry Rd & Glendale Av
								STOP_0220, // != Pen Centre East Entrance
								STOP_PEN, // Pen Centre
						})) //
				.compileBothTripSort());
		map2.put(423L, new RouteTripSpec(423L, //
				0, MTrip.HEADSIGN_TYPE_STRING, WEST_, //
				1, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_0839, // ++
								STOP_MCL, // MacTurnbull Dr & Louth St
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_MCL, // MacTurnbull Dr & Louth St
								STOP_0778, // ++
								STOP_BRU, // Brock University
						})) //
				.compileBothTripSort());
		map2.put(424L, new RouteTripSpec(424L, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT, //
				1, MTrip.HEADSIGN_TYPE_STRING, "Tupper") //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_1030, // ++
								STOP_RIC, // Richmond St & Confederation Av
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_RIC, // Richmond St & Confederation Av
								STOP_1294, // ++
								STOP_BRU, // Brock University
						})) //
				.compileBothTripSort());
		map2.put(436L, new RouteTripSpec(436L, //
				0, MTrip.HEADSIGN_TYPE_STRING, BROCK_UNIVERSITY_SHORT, //
				1, MTrip.HEADSIGN_TYPE_STRING, PEN_CTR) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						STOP_PEN, // Pen Centre
								STOP_0831, //
								STOP_BRU, // Brock University
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						STOP_BRU, // Brock University
								STOP_0842, //
								STOP_PEN, // Pen Centre
						})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
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
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		if (isGoodEnoughAccepted()) {
			if (mRoute.getId() == 88L) {
				if (gTrip.getTripHeadsign().endsWith("AM")) {
					mTrip.setHeadsignString("AM", gTrip.getDirectionId());
					return;
				} else if (gTrip.getTripHeadsign().endsWith("PM")) {
					mTrip.setHeadsignString("PM", gTrip.getDirectionId());
					return;
				}
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 305l) {
			if (Arrays.asList( //
					FAIRVIEW_MALL, //
					DOWNTOWN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 310L) {
			if (Arrays.asList( //
					"Sir Winston", //
					PEN_CTR //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(PEN_CTR, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 401L) {
			if (Arrays.asList( //
					HOSP, //
					DOWNTOWN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 412L) {
			if (Arrays.asList( //
					FAIRVIEW_MALL, //
					DOWNTOWN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 432L) {
			if (Arrays.asList( //
					"Burleigh Hl", //
					BROCK_UNIVERSITY_SHORT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(BROCK_UNIVERSITY_SHORT, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexptected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern STARTS_WITH_RSN_RLN = Pattern.compile("(^[0-9]{1,3}[A-Z]? (([\\w]+[\\.]? )+\\- )*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern CENTR = Pattern.compile("((^|\\W){1}(cent[r]?)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String CENTR_REPLACEMENT = "$2Center$4";

	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^.* to )", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = STARTS_WITH_RSN_RLN.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = STARTS_WITH_TO.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
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
			+ "STC_[F|S|W][0-9]{2,4}_Stop|" //
			+ "STC_[F|S|W][0-9]{2,4}Stop|" //
			+ "STC_[F|S|W][0-9]{2,4}_|" //
			+ "STC_[F|S|W][0-9]{2,4}|" //
			+ "STC_[F|S|W]Stop|" //
			+ "STC_[F|S|W]_Stop|" //
			//
			+ "STC_[F|S|W]_[0-9]{2,4}_Stop|" //
			+ "STC_[F|S|W]_[0-9]{2,4}_|" //
			//
			+ "STC_[F|S|W]_|" //
			+ "STC_[F|S|W]|" //
			//
			+ "[F|S|W][0-9]{4}_Stop|" //
			+ "[F|S|W][0-9]{4}_|" //
			//
			+ "ST_SStop|" //
			+ "ST_S|" //
			//
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
		} else if (stopCode.equals("VIL")) {
			return 100017;
		} else if (stopCode.equals("STK")) {
			return 100018;
		} else if (stopCode.equals("MCS")) {
			return 100019;
		} else if (stopCode.equals("GDC")) {
			return 100020;
		} else if (stopCode.equals("WLC")) {
			return 100021;
		} else if (stopCode.equals("WAL")) {
			return 100022;
		} else if (stopCode.equals("RIC")) {
			return 100023;
		} else if (stopCode.equals("HOS")) {
			return 100024;
		} else if (stopCode.equals("LKO")) {
			return 100025;
		} else if (stopCode.equals("LKV")) {
			return 100026;
		} else if (stopCode.equals("GBH")) {
			return 100027;
		} else if (stopCode.equals("LLI")) {
			return 100028;
		} else if (stopCode.equals("LKN")) {
			return 100029;
		} else if (stopCode.equals("GLI")) {
			return 100030;
		} else if (stopCode.equals("SIR")) {
			return 100031;
		} else if (stopCode.equals("OSD")) {
			return 100032;
		} else if (stopCode.equals("DCA")) {
			return 100033;
		} else if (stopCode.equals("TLQ")) {
			return 100034;
		} else if (stopCode.equals("CTO")) {
			return 100035;
		} else if (stopCode.equals("MCL")) {
			return 100036;
		}
		try {
			Matcher matcher = DIGITS.matcher(stopCode);
			if (matcher.find()) {
				int digits = Integer.parseInt(matcher.group());
				if (stopCode.startsWith(CD)) {
					digits += 30000;
				} else if (stopCode.startsWith(CRL)) {
					digits += 40000;
				} else if (stopCode.startsWith(GLI)) {
					digits += 70000;
				} else if (stopCode.startsWith(LKV)) {
					digits += 120000;
				} else if (stopCode.startsWith(LLI)) {
					digits += 130000;
				} else if (stopCode.startsWith(NOTL)) {
					digits += 140000;
				} else if (stopCode.startsWith(PGL)) {
					digits += 160000;
				} else if (stopCode.startsWith(SCWE)) {
					digits += 190000;
				} else {
					System.out.printf("\nUnexpected stop ID (starts with digits) %s", gStop);
					System.exit(-1);
					digits = -1;
				}
				return digits;
			}
		} catch (Exception e) {
			System.out.printf("\nError while finding stop ID for %s", gStop);
			e.printStackTrace();
		}
		int digits;
		if (stopCode.startsWith(ALNBG)) {
			digits = 100000;
		} else if (stopCode.startsWith(ARTH)) {
			digits = 110000;
		} else if (stopCode.startsWith(BNTG)) {
			digits = 200000;
		} else if (stopCode.startsWith(BRCK)) {
			digits = 210000;
		} else if (stopCode.startsWith(CLRK)) {
			digits = 300000;
		} else if (stopCode.startsWith(CMGS)) {
			digits = 310000;
		} else if (stopCode.startsWith(CNFD)) {
			digits = 320000;
		} else if (stopCode.startsWith(CRLT)) {
			digits = 330000;
		} else if (stopCode.startsWith(CRMT)) {
			digits = 340000;
		} else if (stopCode.startsWith(DNKL)) {
			digits = 400000;
		} else if (stopCode.startsWith(DNTN)) {
			digits = 410000;
		} else if (stopCode.startsWith(FARV)) {
			digits = 600000;
		} else if (stopCode.startsWith(FRTH)) {
			digits = 610000;
		} else if (stopCode.startsWith(GEN)) {
			digits = 700000;
		} else if (stopCode.startsWith(GENV)) {
			digits = 710000;
		} else if (stopCode.startsWith(GLND)) {
			digits = 720000;
		} else if (stopCode.startsWith(GNDL)) {
			digits = 7300000;
		} else if (stopCode.startsWith(GRDG)) {
			digits = 740000;
		} else if (stopCode.startsWith(GRNT)) {
			digits = 750000;
		} else if (stopCode.startsWith(HAIG)) {
			digits = 800000;
		} else if (stopCode.startsWith(HRTZ)) {
			digits = 810000;
		} else if (stopCode.startsWith(KEFR)) {
			digits = 1100000;
		} else if (stopCode.startsWith(LAKE)) {
			digits = 1200000;
		} else if (stopCode.startsWith(LOCK)) {
			digits = 1210000;
		} else if (stopCode.startsWith(LSHR)) {
			digits = 1220000;
		} else if (stopCode.startsWith(MAC_T)) {
			digits = 1300000;
		} else if (stopCode.startsWith(MERT)) {
			digits = 1310000;
		} else if (stopCode.startsWith(MRDL)) {
			digits = 1320000;
		} else if (stopCode.startsWith(NIAG)) {
			digits = 1400000;
		} else if (stopCode.startsWith(NI_FLS)) {
			digits = 1410000;
		} else if (stopCode.startsWith(NW_GN)) {
			digits = 1420000;
		} else if (stopCode.startsWith(ONT) || gStop.getStopName().startsWith(ONTARIO_ST)) {
			digits = 1500000;
		} else if (stopCode.startsWith(ORMD)) {
			digits = 1510000;
		} else if (stopCode.startsWith(PELM)) {
			digits = 1600000;
		} else if (stopCode.startsWith(PEN)) {
			digits = 1610000;
		} else if (stopCode.startsWith(QRVW)) {
			digits = 1700000;
		} else if (stopCode.startsWith(RICH)) {
			digits = 1800000;
		} else if (stopCode.startsWith(RKWD)) {
			digits = 1810000;
		} else if (stopCode.startsWith(SCMN)) {
			digits = 1900000;
		} else if (stopCode.startsWith(SCOT)) {
			digits = 1910000;
		} else if (stopCode.startsWith(SRNG)) {
			digits = 1920000;
		} else if (stopCode.startsWith(ST_D)) {
			digits = 1930000;
		} else if (stopCode.startsWith(ST_P)) {
			digits = 1940000;
		} else if (stopCode.startsWith(ST_PW)) {
			digits = 1950000;
		} else if (stopCode.startsWith(SULV)) {
			digits = 1960000;
		} else if (stopCode.startsWith(TWNL)) {
			digits = 2000000;
		} else if (stopCode.startsWith(VINE)) {
			digits = 2200000;
		} else if (stopCode.startsWith(VSKL)) {
			digits = 2210000;
		} else if (stopCode.startsWith(WAL)) {
			digits = 2300000;
		} else if (stopCode.startsWith(WCTR)) {
			digits = 2310000;
		} else if (stopCode.startsWith(WEST)) {
			digits = 2320000;
		} else if (stopCode.startsWith(WLDW)) {
			digits = 2330000;
		} else if (stopCode.startsWith(WLND)) {
			digits = 2340000;
		} else {
			System.out.printf("\nUnexpected stop ID (starts with) %s", gStop);
			System.exit(-1);
			digits = -1;
		}
		if (stopCode.endsWith(ABBY)) {
			digits += 100;
		} else if (stopCode.endsWith(ALL)) {
			digits += 101;
		} else if (stopCode.endsWith(ARTH)) {
			digits += 102;
		} else if (stopCode.endsWith(BCHN)) {
			digits += 200;
		} else if (stopCode.endsWith(BNTG)) {
			digits += 201;
		} else if (stopCode.endsWith(BRHL)) {
			digits += 202;
		} else if (stopCode.endsWith(CAMP)) {
			digits += 300;
		} else if (stopCode.endsWith(CHUR)) {
			digits += 301;
		} else if (stopCode.endsWith(CLR)) {
			digits += 302;
		} else if (stopCode.endsWith(CMPS)) {
			digits += 303;
		} else if (stopCode.endsWith(CNTR)) {
			digits += 304;
		} else if (stopCode.endsWith(COLL)) {
			digits += 305;
		} else if (stopCode.endsWith(COLR)) {
			digits += 306;
		} else if (stopCode.endsWith(CONF)) {
			digits += 307;
		} else if (stopCode.endsWith(CRLT)) {
			digits += 308;
		} else if (stopCode.endsWith(CUGA)) {
			digits += 309;
		} else if (stopCode.endsWith(ECHR)) {
			digits += 500;
		} else if (stopCode.endsWith(FACR)) {
			digits += 600;
		} else if (stopCode.endsWith(GENV)) {
			digits += 700;
		} else if (stopCode.endsWith(GLMR)) {
			digits += 701;
		} else if (stopCode.endsWith(GLNR)) {
			digits += 702;
		} else if (stopCode.endsWith(GNDL)) {
			digits += 703;
		} else if (stopCode.endsWith(GRNT)) {
			digits += 704;
		} else if (stopCode.endsWith(HOSP)) {
			digits += 800;
		} else if (stopCode.endsWith(HP)) {
			digits += 801;
		} else if (stopCode.endsWith(LAKE)) {
			digits += 1200;
		} else if (stopCode.endsWith(LINW)) {
			digits += 1201;
		} else if (stopCode.endsWith(LOUT)) {
			digits += 1203;
		} else if (stopCode.endsWith(LNHVN)) {
			digits += 1204;
		} else if (stopCode.endsWith(LYN)) {
			digits += 1205;
		} else if (stopCode.endsWith(MAIN)) {
			digits += 1300;
		} else if (stopCode.endsWith(MALL)) {
			digits += 1301;
		} else if (stopCode.endsWith(MART)) {
			digits += 1302;
		} else if (stopCode.endsWith(MC_TB)) {
			digits += 1303;
		} else if (stopCode.endsWith(MERT)) {
			digits += 1304;
		} else if (stopCode.endsWith(MRDL)) {
			digits += 1305;
		} else if (stopCode.endsWith(MRTV)) {
			digits += 1306;
		} else if (stopCode.endsWith(NIAG)) {
			digits += 1400;
		} else if (stopCode.endsWith(OAKD)) {
			digits += 1500;
		} else if (stopCode.endsWith(ONT)) {
			digits += 1501;
		} else if (stopCode.endsWith(PARK)) {
			digits += 1600;
		} else if (stopCode.endsWith(PELM)) {
			digits += 1601;
		} else if (stopCode.endsWith(QUEN)) {
			digits += 1700;
		} else if (stopCode.endsWith(QUNS)) {
			digits += 1701;
		} else if (stopCode.endsWith(RES)) {
			digits += 1800;
		} else if (stopCode.endsWith(RICH)) {
			digits += 1801;
		} else if (stopCode.endsWith(ST_D)) {
			digits += 1900;
		} else if (stopCode.endsWith(TERM)) {
			digits += 2000;
		} else if (stopCode.endsWith(TOWP)) {
			digits += 2001;
		} else if (stopCode.endsWith(TWNL)) {
			digits += 2002;
		} else if (stopCode.endsWith(TUPP)) {
			digits += 2003;
		} else if (stopCode.endsWith(UNIV)) {
			digits += 2100;
		} else if (stopCode.endsWith(VINE)) {
			digits += 2200;
		} else if (stopCode.endsWith(VSKL)) {
			digits += 2201;
		} else if (stopCode.endsWith(WDRW)) {
			digits += 2300;
		} else if (stopCode.endsWith(WLND)) {
			digits += 2301;
		} else if (stopCode.endsWith(WMBL)) {
			digits += 2302;
		} else {
			System.out.printf("\nUnexpected stop ID (ends with) %s", gStop);
			System.exit(-1);
			digits = -1;
		}
		return digits;
	}
}
