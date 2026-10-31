from datetime import datetime, timezone, timedelta
from typing import Dict, Any

def get_market_status(symbol: str = "NIFTY") -> Dict[str, Any]:
    """
    Evaluates real-time market trading session status for Indian Stock Exchanges (NSE / BSE),
    Commodities (MCX), and Crypto.
    
    Trading Hours (IST = UTC+05:30):
    - NSE / BSE Equity & Derivatives: Monday - Friday, 09:15 to 15:30 IST.
      Pre-open: 09:00 to 09:15 IST.
    - MCX Commodities: Monday - Friday, 09:00 to 23:30 / 23:55 IST.
    - Crypto (BTC, ETH): 24/7 Always Open.
    """
    sym = symbol.upper().strip()
    
    # Crypto 24/7
    if sym in ["BTC", "ETH"]:
        return {
            "is_open": True,
            "status": "MARKET OPEN",
            "session_label": "24/7 Global Crypto Session",
            "next_session": "Continuous 24/7 Trading",
            "last_close_time": datetime.now(timezone(timedelta(hours=5, minutes=30))).strftime("%d-%b-%Y %I:%M %p IST")
        }

    ist = timezone(timedelta(hours=5, minutes=30))
    now_ist = datetime.now(ist)
    weekday = now_ist.weekday() # 0 = Mon, 4 = Fri, 5 = Sat, 6 = Sun
    minute_of_day = now_ist.hour * 60 + now_ist.minute
    
    is_commodity = sym in ["CRUDEOIL", "GOLD", "SILVER", "NATURALGAS", "COPPER", "ZINC", "ALUMINIUM"]
    
    if is_commodity:
        # MCX hours: Mon-Fri 09:00 to 23:30
        if 0 <= weekday <= 4:
            if 9 * 60 <= minute_of_day < 23 * 60 + 30:
                return {
                    "is_open": True,
                    "status": "MARKET OPEN",
                    "session_label": "Live MCX Commodity Trading",
                    "next_session": "Session closes today at 11:30 PM IST",
                    "last_close_time": now_ist.strftime("%d-%b-%Y %I:%M %p IST")
                }
        next_open = "Monday at 09:00 AM IST" if weekday >= 4 else "Tomorrow at 09:00 AM IST"
        return {
            "is_open": False,
            "status": "MARKET CLOSED",
            "session_label": "Official MCX Commodity Close",
            "next_session": f"Next Session: {next_open}",
            "last_close_time": now_ist.strftime("%d-%b-%Y 11:30 PM IST")
        }

    # Standard NSE / BSE Equities & Indices
    is_weekday = 0 <= weekday <= 4
    market_open = 9 * 60 + 15   # 09:15 AM IST
    market_close = 15 * 60 + 30 # 03:30 PM IST
    pre_open = 9 * 60           # 09:00 AM IST

    if is_weekday:
        if market_open <= minute_of_day < market_close:
            return {
                "is_open": True,
                "status": "MARKET OPEN",
                "session_label": "Live Regular Trading Session",
                "next_session": "Regular Session closes today at 03:30 PM IST",
                "last_close_time": now_ist.strftime("%d-%b-%Y %I:%M %p IST")
            }
        elif pre_open <= minute_of_day < market_open:
            return {
                "is_open": False,
                "status": "PRE-OPEN",
                "session_label": "Pre-Market Discovery Session",
                "next_session": "Normal Trading begins at 09:15 AM IST",
                "last_close_time": now_ist.strftime("%d-%b-%Y 09:00 AM IST")
            }

    # Market is CLOSED
    if weekday == 4 and minute_of_day >= market_close:
        next_open = "Monday at 09:15 AM IST"
    elif weekday == 5: # Saturday
        next_open = "Monday at 09:15 AM IST"
    elif weekday == 6: # Sunday
        next_open = "Tomorrow (Monday) at 09:15 AM IST"
    elif minute_of_day >= market_close:
        next_open = "Tomorrow at 09:15 AM IST"
    else:
        next_open = "Today at 09:15 AM IST"

    return {
        "is_open": False,
        "status": "MARKET CLOSED",
        "session_label": "Official Market Closing Session",
        "next_session": f"Next Session: {next_open}",
        "last_close_time": now_ist.strftime("%d-%b-%Y 03:30 PM IST")
    }
