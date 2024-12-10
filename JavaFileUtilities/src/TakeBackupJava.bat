
java DirectoryCopy.java source="C:/dennis" target="D:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\C_to_D_dennis_backup.log" do_not_move_extra_files_folders_in_target="D:\dennis\MiscLargeFiles" move_extra_files_to="D:\temp\moved_files\delete_later\extra_files\c_to_d" exclude_from_copy="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache "

java DirectoryCopy.java source="D:/dennis" target="E:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_E_dennis_backup.log" do_not_move_extra_files_folders_in_target=""  move_extra_files_to="E:\temp\moved_files\delete_later\extra_files\d_to_e" exclude_from_copy="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data "


REM java DirectoryCopy.java source="D:/dennis" target="\\t03\d\dennis" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_DennisThink_D_dennis_backup.log"  move_extra_files_to="\\t03\d\temp\moved_files\delete_later\extra_files\d_to_d_dennis" exclude_files_dirs="\.git\, \.class, \.settings\, \target\, \bin\ "


REM java DirectoryCopy.java source="D:\dennis\MiscLargeFiles\bt_data" target="\\TC01\d_on_t01\dennis\MiscLargeFiles\bt_data" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_TC01_d_backup.log"  move_extra_files_to="\\TC01\d_on_t01\temp\moved_files\delete_later\extra_files\D_to_TC01_d" exclude_files_dirs="\.git\, \.class, \.settings\, \target\, \bin\ "



echo "All copying done"
pause