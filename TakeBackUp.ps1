
function copyFolder($src,$dest,$exclude){
        # param(
           #  [Parameter(Mandatory=$true,Position=0)] [String]$src,
           #  [Parameter(Mandatory=$true,Position=1)] [String[]]$dest,
           #  [Parameter(Mandatory=$false,Position=2)] [String[]]$exclude
       #  )

    $timeStamp = get-date -Format "yyyy-MM-dd#HH.mm.ss"
    $backuplogfile="E:\temp\backuplogs\backup"+$timeStamp+".log"

    $excludeStr=""
    if($exclude -ne $null){
        $excludeStr = $exclude -join " "
        $excludeStr = $excludeStr.Trim()
        #Write-Host "-----"$exclude"----"
    }


    foreach ($d in $dest) {
         if($excludeStr -eq ""){
              robocopy  $src $d /E /FFT /r:1 /log+:$backuplogfile
        }else{
              robocopy  $src $d /E /FFT /xd $exclude /r:1 /log+:$backuplogfile
        }

        if ($lastexitcode -eq 16){
            write-host "Copy  not succeeded to " $d -ForegroundColor Red
        }else{
            Write-Host "Copy  succeeded to "  $d -ForegroundColor Green
        }

    }
    Write-Host ""

 
}#end of copyFolder


$baseDirE="E:\dennis"
$baseDirC="C:\dennis"
$baseDirWin8T440="\\Denniswin10t440\c\dennis"
$baseDirWin10T550="\\Denniswin10t530\c\dennis"
$baseDirF="F:\dennis"


$baseDirs=@($baseDirC,$baseDirF,$baseDirWin8T440,$baseDirWin10T550)



$excludeDir1="E:\dennis\ToDo"
$excludeDir2="E:\dennis\miscstuff"

$excludeDirs=@($excludeDir1,$excludeDir2)

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying basic stuff"
Write-Host "-------------------"
$src=$baseDirE
$dest=$baseDirs

copyFolder $src $dest $excludeDirs

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying health stuff"
Write-Host "-------------------"
$health="\miscstuff\Health\HealthRecords"
$src=$baseDirE+$health
$dest=$baseDirs.Clone()


for ($i=0; $i -lt $dest.Length; $i++) {
    $dest[$i]=$dest[$i]+$health;
}

copyFolder $src $dest



#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying Marshall Protocol stuff"
Write-Host "------------------------------"
$mp="\miscstuff\Health\MarshallProtocol"
$src=$baseDirE+$mp
$dest=$baseDirs.Clone()


for ($i=0; $i -lt $dest.Length; $i++) {
    $dest[$i]=$dest[$i]+$mp;
}
copyFolder $src $dest


#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying ThunderBird Emails"
Write-Host "-------------------------"


$emailFolder="\miscstuff\EmailBackup\Thunderbird\Profiles"
$src="C:\Users\Lenovo\AppData\Roaming\Thunderbird\Profiles"

Write-Host "Copying ThunderBird Emails from C to E (True copy) drive  "
$dest=$baseDirE+$emailFolder
copyFolder $src $dest



$dest=$baseDirs.Clone()


for ($i=0; $i -lt $dest.Length; $i++) {
    $dest[$i]=$dest[$i]+$emailFolder;
}
copyFolder $src $dest

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
#Copy entire E drive
Write-Host "Copying entire E drive"
Write-Host "-----------------------"

$src=$baseDirE
$dest=$baseDirF

copyFolder $src $dest

Write-Host "-----Done--------"


