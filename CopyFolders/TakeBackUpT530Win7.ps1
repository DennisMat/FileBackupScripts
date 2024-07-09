
function copyFolder($src,$dest,$exclude){


    $timeStamp = get-date -Format "yyyy-MM-dd#HH.mm.ss"
    $backuplogfile="D:\temp\backuplogs\backup"+$timeStamp+".log"

    $excludeStr=""
    if($exclude -ne $null){
        $excludeStr = $exclude -join '" "'
        $excludeStr = $excludeStr.Trim()
        #Write-Host "-----"$exclude"----"
    }

    $excludeStr = -join(' "',$excludeStr,'"' ,' ".git" ".metadata" ')

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


$baseSrcDir="Z:\dennis"
$baseDirC="C:\dennis"
$baseDirD="D:\dennis"
$baseDirE="E:\dennis"

$baseDirs=@($baseDirC,$baseDirD,$baseDirE)
$excludeDir1=$baseSrcDir+"\ToDo"
$excludeDir2=$baseSrcDir+"\miscstuff"

$excludeDirs=@($excludeDir1,$excludeDir2)

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x

Write-Host "Copying basic stuff to all drives"
Write-Host "-------------------"
$src=$baseSrcDir
$dest=$baseDirs
copyFolder $src $dest $excludeDirs

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying HealthRecords stuff to all drives"
Write-Host "-----------------------------------"
$health="\miscstuff\Health\HealthRecords"
$src=$baseSrcDir+$health
$dest=$baseDirs.Clone()


for ($i=0; $i -lt $dest.Length; $i++) {
    $dest[$i]=$dest[$i]+$health;
}

copyFolder $src $dest



#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying Marshall Protocol stuff to all drives"
Write-Host "----------------------------------------------"
$mp="\miscstuff\Health\MarshallProtocol"
$src=$baseSrcDir+$mp
$dest=$baseDirs.Clone()


for ($i=0; $i -lt $dest.Length; $i++) {
    $dest[$i]=$dest[$i]+$mp;
}
copyFolder $src $dest


#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying ThunderBird Emails from C to D"
Write-Host "------------------------------------------"


$emailFolder="\miscstuff\EmailBackup\Thunderbird"
$src="C:\Users\dennis\AppData\Roaming\Thunderbird"

Write-Host "Copying ThunderBird Emails from C to D (True copy) drive  "
$dest=$baseDirD+$emailFolder
copyFolder $src $dest


#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
Write-Host "Copying entire Z drive to D"
Write-Host "-----------------------"

$src=$baseSrcDir
$dest=$baseDirD

copyFolder $src $dest

#-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x-------x
#Copy entire Z drive
Write-Host "Copying entire Z drive to E"
Write-Host "-----------------------"

$src=$baseSrcDir
$dest=$baseDirE

copyFolder $src $dest




