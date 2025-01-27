REM copy core files C to D
java DirectoryCopy.java source="C:/dennis" target="D:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\C_to_D_dennis_backup.log" do_not_move_extra_files_folders_in_target="D:\dennis\MiscLargeFiles" move_extra_files_to="D:\temp\moved_files\delete_later\extra_files\c_to_d" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache "


REM copy D to E, exclude bt files
java DirectoryCopy.java source="D:/dennis" target="E:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_E_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="E:\temp\moved_files\delete_later\extra_files\d_to_e" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data "

REM copy core files D to tc01:F , exclude bt files
java DirectoryCopy.java source="D:/dennis" target="\\Tc01\f\dennis"  log_file="C:\Users\admin\Desktop\Shortcuts\D_to_tc01_F_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="\\Tc01\f\temp\moved_files\delete_later\extra_files\d_to_tc01_f" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data "

REM copy core files D to tc01:G , exclude bt files
java DirectoryCopy.java source="D:/dennis" target="\\Tc01\g\dennis"  log_file="C:\Users\admin\Desktop\Shortcuts\D_to_tc01_G_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="\\Tc01\g\temp\moved_files\delete_later\extra_files\d_to_tc01_g" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data "


REM copy only bt files
java DirectoryCopy.java source="\\Tc01\d\dennis\MiscLargeFiles\bt_data" target="D:\dennis\MiscLargeFiles\bt_data" log_file="C:\Users\admin\Desktop\Shortcuts\t01_to_D_dennis_backup.log" move_extra_files_to="D:\temp\moved_files\delete_later\extra_files\t01_to_d_bt_data"





echo "All copying done"
pause