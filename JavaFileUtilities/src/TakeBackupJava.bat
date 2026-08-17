cd C:\dennis\work\FileBackupScripts\JavaFileUtilities\src
REM  /f parameter fixes errors on the disk, 
REM /r (which implies /f) also checks for bad sectors and recovers data, 
REM  /x forces the volume to dismount to allow the scan and repair process to proceed
REM copy core files C to D
REM chkdsk d: /f /r /x

REM copy C to D .Core files 
java DirectoryCopy.java source="C:/dennis" target="D:/dennis" log_file="D:\temp\BackupLogs\C_to_D_dennis_backup.log" do_not_move_extra_files_folders_in_target="D:\dennis\MiscLargeFiles" move_extra_files_to="D:\temp\moved_files\delete_later\extra_files\c_to_d" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache, \modernize-free-angular-v4\main\.angular\, \modernize-free-angular-v4\main\node_modules\,\modernize-free-angular-v4\main\dist\ "

REM copy C to E .Core files 
REM java DirectoryCopy.java source="C:/dennis" target="E:/dennis" log_file="D:\temp\BackupLogs\C_to_E_dennis_backup.log" do_not_move_extra_files_folders_in_target="E:\dennis\MiscLargeFiles" move_extra_files_to="E:\temp\moved_files\delete_later\extra_files\c_to_e" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache, \modernize-free-angular-v4\main\.angular\, \modernize-free-angular-v4\main\node_modules\,\modernize-free-angular-v4\main\dist\"

REM copy C to \\Tc01\f .Core files 
java DirectoryCopy.java source="C:/dennis" target="\\Tc01\f\dennis" log_file="D:\temp\BackupLogs\C_to_tc01_F_dennis_backup.log" do_not_move_extra_files_folders_in_target="\\Tc01\f\dennis\MiscLargeFiles" move_extra_files_to="\\Tc01\f\temp\moved_files\delete_later\extra_files\c_to_tc01_F" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache, \modernize-free-angular-v4\main\.angular\, \modernize-free-angular-v4\main\node_modules\,\modernize-free-angular-v4\main\dist\"

REM copy C to \\Tc01\g .Core files 
java DirectoryCopy.java source="C:/dennis" target="\\Tc01\g\dennis" log_file="D:\temp\BackupLogs\C_to_tc01_G_dennis_backup.log" do_not_move_extra_files_folders_in_target="\\Tc01\g\dennis\MiscLargeFiles" move_extra_files_to="\\Tc01\g\temp\moved_files\delete_later\extra_files\c_to_tc01_G" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache, \modernize-free-angular-v4\main\.angular\, \modernize-free-angular-v4\main\node_modules\,\modernize-free-angular-v4\main\dist\"

REM copy D to E, MiscLargeFiles files, exclude bt files
REM chkdsk e: /f /r /x
REM java DirectoryCopy.java source="D:/dennis/MiscLargeFiles" target="E:/dennis/MiscLargeFiles" log_file="D:\temp\BackupLogs\D_to_E_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="E:\temp\moved_files\delete_later\extra_files\d_to_e" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data, tools\tportable-x64.4.3.1"

REM copy  D to tc01:F , MiscLargeFiles files, exclude bt files
java DirectoryCopy.java source="D:/dennis/MiscLargeFiles" target="\\Tc01\f\dennis\MiscLargeFiles"  log_file="D:\temp\BackupLogs\D_to_tc01_F_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="\\Tc01\f\temp\moved_files\delete_later\extra_files\d_to_tc01_f" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data, tools\tportable-x64.4.3.1 "

REM copy  D to tc01:G , MiscLargeFiles files, exclude bt files
java DirectoryCopy.java source="D:/dennis/MiscLargeFiles" target="\\Tc01\g\dennis\MiscLargeFiles"  log_file="D:\temp\BackupLogs\D_to_tc01_G_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="\\Tc01\g\temp\moved_files\delete_later\extra_files\d_to_tc01_g" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data, tools\tportable-x64.4.3.1 "


REM copy only bt files
REM java DirectoryCopy.java source="\\Tc01\d\dennis\MiscLargeFiles\bt_data" target="D:\dennis\MiscLargeFiles\bt_data" log_file="D:\temp\BackRabbitupLogs\t01_to_D_dennis_backup.log" move_extra_files_to="D:\temp\moved_files\delete_later\extra_files\t01_to_d_bt_data"

REM for j drive on tc01
REM cd C:\dennis\work\FileBackupScripts\JavaFileUtilities\src
REM java DirectoryCopy.java source="C:/dennis" target="\\Tc01\j\dennis" log_file="D:\temp\BackupLogs\C_to_tc01_J_dennis_backup.log" do_not_move_extra_files_folders_in_target="\\Tc01\j\dennis\MiscLargeFiles" move_extra_files_to="\\Tc01\j\temp\moved_files\delete_later\extra_files\c_to_tc01_J" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache, \modernize-free-angular-v4\main\.angular\, \modernize-free-angular-v4\main\node_modules\,\modernize-free-angular-v4\main\dist\"
REM java DirectoryCopy.java source="D:/dennis/MiscLargeFiles" target="\\Tc01\j\dennis\MiscLargeFiles"  log_file="D:\temp\BackupLogs\D_to_tc01_J_dennis_backup.log" do_not_move_extra_files_folders_in_target="MiscLargeFiles\bt_data" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data, tools\tportable-x64.4.3.1 "





echo "All copying done"
pause