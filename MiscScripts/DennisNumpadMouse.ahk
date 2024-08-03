#SingleInstance force
#MaxHotkeysPerInterval 500

       
#InstallKeybdHook

Temp = 0
Temp2 = 0


SetKeyDelay, -1
SetMouseDelay, -1

Hotkey, *Alt, ButtonLeftClick
Hotkey, *F1, ButtonDoubleClick


Gosub, ~NumLock  ; Initialize based on current NumLock state.
return

;Key activation support

~NumLock::
; Wait for it to be released because otherwise the hook state gets reset
; while the key is down, which causes the up-event to get suppressed,
; which in turn prevents toggling of the NumLock state/light:
KeyWait, NumLock
GetKeyState, NumLockState, NumLock, T
If NumLockState = D
{
	Hotkey, *Alt, on
	Hotkey, *F1, on


}
else
{
	Hotkey, *Alt, off
	Hotkey, *F1, off


}
return

ButtonLeftClick:
GetKeyState, already_down_state, LButton
If already_down_state = D
	return
Button2 = Alt
ButtonClick = Left
Goto ButtonClickStart

return

ButtonDoubleClick:
GetKeyState, already_down_state, LButton
If already_down_state = D
	return
Button2 = F1
ButtonClick = Left
Click 2
return

ButtonClickStart:
MouseClick, %ButtonClick%,,, 1, 0, D
SetTimer, ButtonClickEnd, 5
return

ButtonClickEnd:
GetKeyState, kclickstate, %Button2%, P
if kclickstate = D
	return

SetTimer, ButtonClickEnd, off
MouseClick, %ButtonClick%,,, 1, 0, U
return