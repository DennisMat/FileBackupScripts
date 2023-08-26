
function copyFolder($src,$dest,$exclude){
        # param(
           #  [Parameter(Mandatory=$true,Position=0)] [String]$src,
           #  [Parameter(Mandatory=$true,Position=1)] [String[]]$dest,
           #  [Parameter(Mandatory=$false,Position=2)] [String[]]$exclude
       #  )

    $timeStamp = get-date -Format "yyyy-MM-dd#HH.mm.ss"
    $backuplogfile="D:\temp\backuplogs\backup"+$timeStamp+".log"

    $excludeStr=""
    if($exclude -ne $null){
        $excludeStr = $exclude -join '" "'
        $excludeStr = $excludeStr.Trim()
        #Write-Host "-----"$exclude"----"
    }

    $excludeStr = -join(' "',$excludeStr,'"' ,' ".git" ')

    foreach ($d in $dest) {
        $robocopyArgs= '"' + $src + '" "' + $d + '" /E /FFT /xd ' + $excludeStr +' /r:1 /log:'+ $backuplogfile +' /TEE'
        #Write-Host $robocopyArgs
        Start-Process -FilePath "robocopy.exe" -ArgumentList $robocopyArgs -Wait

        # Above work but for some reason the exit code is 16
        if ($LASTEXITCODE -eq 16){
            #write-host "Copy  not succeeded to " $d -ForegroundColor Red
        }else{
            Write-Host "Copy  succeeded to "  $d -ForegroundColor Green
        }

    }
    Write-Host ""

 
}#end of copyFolder

#source dir located at Denniswin10t530
$baseSourceDir="C:\dennis"

$baseDestDirD="D:\dennis"


#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
#Copy entire D drive
Write-Host "Copying entire C drive to D" -ForegroundColor Green
Write-Host "----------------------------" -ForegroundColor Green

$src=$baseSourceDir
$dest=$baseDestDirD

copyFolder $src $dest 




