from typing import List, Dict, Any, Optional
from datetime import datetime
from app.models.schemas import (
    ResultsAndEventsResponse,
    QuarterlyResultItem,
    CorporateEventItem,
    EconomicEventItem
)
from app.services.market_data_fetcher import MarketDataFetcher

class ResultsAndEventsService:
    """
    Service for Corporate Earnings Calendar, Board Meetings, and Macroeconomic Events.
    Covers:
    - Upcoming and Declared Quarterly Earnings (Revenue & Net Profit vs Estimates)
    - Corporate Events (Board Meetings, Fund Raising, Capital Restructuring)
    - High-Impact Macroeconomic Data (RBI MPC Policy, US Fed Rate Decision, CPI, WPI, GDP, OPEC)
    """

    _QUARTERLY_RESULTS: List[Dict[str, Any]] = [
        {
            "id": "RES_TCS_Q2",
            "symbol": "TCS",
            "company_name": "Tata Consultancy Services Ltd",
            "result_date": "10-Oct-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 64259.0,
            "revenue_estimate_cr": 63800.0,
            "pat_actual_cr": 11909.0,
            "pat_estimate_cr": 11650.0,
            "yoy_growth_pct": 5.0,
            "status": "DECLARED",
            "purpose": "Financial Results & Interim Dividend"
        },
        {
            "id": "RES_INFY_Q2",
            "symbol": "INFY",
            "company_name": "Infosys Ltd",
            "result_date": "17-Oct-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 40986.0,
            "revenue_estimate_cr": 40500.0,
            "pat_actual_cr": 6506.0,
            "pat_estimate_cr": 6420.0,
            "yoy_growth_pct": 4.7,
            "status": "DECLARED",
            "purpose": "Financial Results & Interim Dividend"
        },
        {
            "id": "RES_RELIANCE_Q2",
            "symbol": "RELIANCE",
            "company_name": "Reliance Industries Ltd",
            "result_date": "14-Oct-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 235480.0,
            "revenue_estimate_cr": 238000.0,
            "pat_actual_cr": 16563.0,
            "pat_estimate_cr": 16900.0,
            "yoy_growth_pct": -4.8,
            "status": "DECLARED",
            "purpose": "Financial Results & 1:1 Bonus Approval"
        },
        {
            "id": "RES_HDFCBANK_Q2",
            "symbol": "HDFCBANK",
            "company_name": "HDFC Bank Ltd",
            "result_date": "19-Oct-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 85500.0,
            "revenue_estimate_cr": 84200.0,
            "pat_actual_cr": 16821.0,
            "pat_estimate_cr": 16500.0,
            "yoy_growth_pct": 5.3,
            "status": "DECLARED",
            "purpose": "Financial Results"
        },
        {
            "id": "RES_ICICIBANK_Q2",
            "symbol": "ICICIBANK",
            "company_name": "ICICI Bank Ltd",
            "result_date": "26-Oct-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 47714.0,
            "revenue_estimate_cr": 46200.0,
            "pat_actual_cr": 11746.0,
            "pat_estimate_cr": 11100.0,
            "yoy_growth_pct": 14.5,
            "status": "DECLARED",
            "purpose": "Financial Results"
        },
        {
            "id": "RES_SBIN_Q2",
            "symbol": "SBIN",
            "company_name": "State Bank of India",
            "result_date": "08-Nov-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 129141.0,
            "revenue_estimate_cr": 125000.0,
            "pat_actual_cr": 18331.0,
            "pat_estimate_cr": 17200.0,
            "yoy_growth_pct": 28.0,
            "status": "DECLARED",
            "purpose": "Financial Results"
        },
        {
            "id": "RES_TATAMOTORS_Q2",
            "symbol": "TATAMOTORS",
            "company_name": "Tata Motors Ltd",
            "result_date": "08-Nov-2026",
            "quarter": "Q2 FY27",
            "revenue_actual_cr": 101450.0,
            "revenue_estimate_cr": 103000.0,
            "pat_actual_cr": 3343.0,
            "pat_estimate_cr": 3600.0,
            "yoy_growth_pct": -11.0,
            "status": "DECLARED",
            "purpose": "Financial Results"
        },
        {
            "id": "RES_MARUTI_Q3",
            "symbol": "MARUTI",
            "company_name": "Maruti Suzuki India Ltd",
            "result_date": "28-Jan-2027",
            "quarter": "Q3 FY27",
            "revenue_actual_cr": None,
            "revenue_estimate_cr": 38500.0,
            "pat_actual_cr": None,
            "pat_estimate_cr": 3950.0,
            "yoy_growth_pct": None,
            "status": "UPCOMING",
            "purpose": "Quarterly Financial Results"
        },
        {
            "id": "RES_TCS_Q3",
            "symbol": "TCS",
            "company_name": "Tata Consultancy Services Ltd",
            "result_date": "09-Jan-2027",
            "quarter": "Q3 FY27",
            "revenue_actual_cr": None,
            "revenue_estimate_cr": 65500.0,
            "pat_actual_cr": None,
            "pat_estimate_cr": 12200.0,
            "yoy_growth_pct": None,
            "status": "UPCOMING",
            "purpose": "Quarterly Financial Results & Interim Dividend"
        },
        {
            "id": "RES_INFY_Q3",
            "symbol": "INFY",
            "company_name": "Infosys Ltd",
            "result_date": "16-Jan-2027",
            "quarter": "Q3 FY27",
            "revenue_actual_cr": None,
            "revenue_estimate_cr": 41800.0,
            "pat_actual_cr": None,
            "pat_estimate_cr": 6650.0,
            "yoy_growth_pct": None,
            "status": "UPCOMING",
            "purpose": "Quarterly Financial Results"
        }
    ]

    _CORPORATE_EVENTS: List[Dict[str, Any]] = [
        {
            "id": "EVT_RELIANCE_BM_2026",
            "symbol": "RELIANCE",
            "company_name": "Reliance Industries Ltd",
            "event_date": "18-Nov-2026",
            "category": "AGM",
            "title": "Extraordinary General Meeting (EGM)",
            "description": "Shareholder voting on Green Energy demerger & subsidiary value unlocking"
        },
        {
            "id": "EVT_TCS_DIV_2026",
            "symbol": "TCS",
            "company_name": "Tata Consultancy Services Ltd",
            "event_date": "18-Nov-2026",
            "category": "DIVIDEND",
            "title": "Record Date for Second Interim Dividend",
            "description": "Record date for payment of ₹28/share interim dividend"
        },
        {
            "id": "EVT_SBIN_FUND_2026",
            "symbol": "SBIN",
            "company_name": "State Bank of India",
            "event_date": "24-Nov-2026",
            "category": "FUND_RAISING",
            "title": "Board Meeting for Capital Raising",
            "description": "Consideration of AT-1 Tier 2 bond issuance up to ₹10,000 Crore"
        },
        {
            "id": "EVT_TATAMOTORS_SPLIT_2026",
            "symbol": "TATAMOTORS",
            "company_name": "Tata Motors Ltd",
            "event_date": "05-Dec-2026",
            "category": "BONUS/SPLIT",
            "title": "Demerger Scheme Implementation",
            "description": "Commercial Vehicles (CV) and Passenger Vehicles (PV+EV) dual listed entities roadmap"
        },
        {
            "id": "EVT_HDFCBANK_BM_2026",
            "symbol": "HDFCBANK",
            "company_name": "HDFC Bank Ltd",
            "event_date": "15-Dec-2026",
            "category": "RESULTS",
            "title": "Analyst & Investor Meet",
            "description": "HDFC Credila stake liquidation and credit-deposit ratio trajectory presentation"
        }
    ]

    _ECONOMIC_EVENTS: List[Dict[str, Any]] = [
        {
            "event_id": "ECO_RBI_MPC_DEC2026",
            "title": "RBI Monetary Policy Committee (MPC) Rate Decision",
            "date": "06-Dec-2026",
            "impact_level": "HIGH",
            "forecast": "6.50% (Status Quo / Neutral Stance Shift)",
            "previous": "6.50%",
            "actual": None,
            "sector_impact": "Banking, NBFCs, Real Estate, Auto"
        },
        {
            "event_id": "ECO_INDIA_CPI_NOV2026",
            "title": "India CPI Consumer Inflation (MoM / YoY)",
            "date": "12-Nov-2026",
            "impact_level": "HIGH",
            "forecast": "5.80%",
            "previous": "5.49%",
            "actual": "6.21%",
            "sector_impact": "Broad Markets, FMCG, Consumption"
        },
        {
            "event_id": "ECO_INDIA_WPI_NOV2026",
            "title": "India Wholesale Price Index (WPI) Inflation",
            "date": "14-Nov-2026",
            "impact_level": "MEDIUM",
            "forecast": "2.20%",
            "previous": "1.84%",
            "actual": "2.36%",
            "sector_impact": "Manufacturing, Metals, Cement, Chemicals"
        },
        {
            "event_id": "ECO_US_FED_FOMC_DEC2026",
            "title": "US Federal Reserve FOMC Interest Rate Decision",
            "date": "18-Dec-2026",
            "impact_level": "HIGH",
            "forecast": "4.50% (-25 bps cut)",
            "previous": "4.75%",
            "actual": None,
            "sector_impact": "IT Services, Pharma, Foreign Capital Inflows (FII)"
        },
        {
            "event_id": "ECO_INDIA_GDP_Q2",
            "title": "India Q2 GDP Growth Rate (YoY)",
            "date": "29-Nov-2026",
            "impact_level": "HIGH",
            "forecast": "6.70%",
            "previous": "6.70%",
            "actual": None,
            "sector_impact": "Infrastructure, Capital Goods, Banks"
        },
        {
            "event_id": "ECO_OPEC_MEETING_DEC2026",
            "title": "OPEC+ Ministerial Crude Output Meeting",
            "date": "01-Dec-2026",
            "impact_level": "HIGH",
            "forecast": "Extension of voluntary output cuts",
            "previous": "Supply curtailment maintained",
            "actual": None,
            "sector_impact": "Oil & Gas, Paints, Aviation, Tyres"
        }
    ]

    @classmethod
    def get_results_and_events(cls, symbol: Optional[str] = None) -> ResultsAndEventsResponse:
        results = cls._QUARTERLY_RESULTS
        events = cls._CORPORATE_EVENTS
        economic = cls._ECONOMIC_EVENTS

        if symbol and symbol.upper() != "ALL":
            clean_sym = symbol.upper().strip()
            # Filter results for this stock if present
            sym_results = [r for r in results if r["symbol"] == clean_sym]
            if sym_results:
                results = sym_results
            else:
                # Synthesize a realistic upcoming quarterly result for this stock
                quote = MarketDataFetcher.get_realtime_quote(clean_sym)
                spot = quote.get("spot_price", 1000.0)
                co_name = quote.get("company_name", f"{clean_sym} Ltd")
                custom_result = {
                    "id": f"RES_{clean_sym}_Q3",
                    "symbol": clean_sym,
                    "company_name": co_name,
                    "result_date": "24-Jan-2027",
                    "quarter": "Q3 FY27",
                    "revenue_actual_cr": None,
                    "revenue_estimate_cr": round(spot * 38.5, 1),
                    "pat_actual_cr": None,
                    "pat_estimate_cr": round(spot * 4.8, 1),
                    "yoy_growth_pct": None,
                    "status": "UPCOMING",
                    "purpose": "Quarterly Financial Results"
                }
                results = [custom_result] + [r for r in cls._QUARTERLY_RESULTS if r["symbol"] != clean_sym]

            # Filter corporate events
            sym_events = [e for e in events if e["symbol"] == clean_sym]
            if sym_events:
                events = sym_events
            else:
                custom_evt = {
                    "id": f"EVT_{clean_sym}_BM_2026",
                    "symbol": clean_sym,
                    "company_name": f"{clean_sym} Ltd",
                    "event_date": "12-Dec-2026",
                    "category": "RESULTS",
                    "title": "Quarterly Board Meeting",
                    "description": f"Board meeting of {clean_sym} to approve Q3 unaudited financial statements"
                }
                events = [custom_evt] + [e for e in cls._CORPORATE_EVENTS if e["symbol"] != clean_sym]

        result_items = [QuarterlyResultItem(**r) for r in results]
        event_items = [CorporateEventItem(**e) for e in events]
        economic_items = [EconomicEventItem(**ec) for ec in economic]

        return ResultsAndEventsResponse(
            symbol=symbol,
            quarterly_results=result_items,
            corporate_events=event_items,
            economic_events=economic_items
        )

