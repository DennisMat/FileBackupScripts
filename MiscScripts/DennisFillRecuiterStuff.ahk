#Requires AutoHotkey v2.0
#SingleInstance Force

	^n::
	{ 
		SendText "Dennis Mathew"
		SoundBeep
	}
	
	^s::
	{ 
		  if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey < 400){
			
			SendText "9346"
			; reload
		}
		else if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey > 400 && A_TimeSincePriorHotkey < 2000){
			;pause ("Toggle", 1)
			SendText "dennisthatsme"
		}
		SoundBeep
	}
	
	^e::
	{ 	if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey < 400){
			SendText "dennis.mathew2000@gmail.com"
			
		}else if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey > 400 && A_TimeSincePriorHotkey < 2000){
			SendText "Bachelor's in Technology the year 1997  from University of Kerala, India . Specialization: Applied Electronics and Instrumentation."
		}
		SoundBeep
	}

	
	^p::
	{ 

	    if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey < 400){
			SendText "412-440-8865"
			reload
		}
		else if (A_PriorHotkey = A_ThisHotkey && A_TimeSincePriorHotkey > 400 && A_TimeSincePriorHotkey < 2000){
			;pause ("Toggle", 1)
			SendText "HP061691"
		}
		SoundBeep
		
	}
	
	^l::
	{ 
		SendText "https://www.linkedin.com/in/dennismathew/"
		SoundBeep
	}
	
	^a::
	{ 
		SendText "7630 Horizon Line Drive, Raleigh, NC 27617"
		SoundBeep
		
	}
	
	^r::
	{ 

		SendText "Ref1: Name: Bash Anoth , Title: Product Owner , Organization: Eqifax , Current Email ID:  Bash.anoth@ehi.com , Contact: (415) 577-2274 , Relationship: Product owner of Team. ref2: Name: Ninad Hule , Title: Senior Architect , Organization: Infosys , Current Email ID:  ninad_hulle@infosys.com   , Contact: +91 9890 535358 Relationship: Peer"
		SoundBeep
	}

	^w::
	{ 
		SendText "H1 with approved I 140, valid up to Sept 2023. A copy can be found at https://tinyurl.com/8l49bnwh"
		SoundBeep
	}

	^i::
	{ 
		SendText "April 25 : 9 a.m to 5 p.m EST, April 26 : 9 a.m to 5 p.m EST , April 27 : 9 a.m to 5 p.m EST"
		SoundBeep
	}
	
	^d::
	{ 
		SendText "04/02 ( April 2)"
		SoundBeep
	}