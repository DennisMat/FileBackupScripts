
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
        $excludeStr = $exclude -join " "
        $excludeStr = $excludeStr.Trim()
        #Write-Host "-----"$exclude"----"
    }

    $excludeStr = -join($excludeStr ," .git")

    foreach ($d in $dest) {
         if($excludeStr -eq ""){
              robocopy  $src $d /E /FFT /r:1 /log+:$backuplogfile /TEE
        }else{
              robocopy  $src $d /E /FFT /xd $excludeStr /r:1 /log+:$backuplogfile /TEE
        }

        if ($lastexitcode -eq 16){
            write-host "Copy  not succeeded to " $d -ForegroundColor Red
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
Write-Host "Copying entire C drive to D"
Write-Host "-----------------------"

$src=$baseSourceDir
$dest=$baseDestDirD

$excludeDir1="C:\dennis\ToDo"
$excludeDir2="C:\dennis\miscstuff"

$excludeDirs=@($excludeDir1,$excludeDir2)

copyFolder $src $dest $excludeDirs




