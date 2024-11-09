
java DirectoryCopy.java source="C:/dennis" target="D:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\C_to_D_dennis_backup.log" do_not_list_extra_files_folders_in_target="D:\dennis\MiscLargeFiles" exclude_files_dirs="\.git\, .class, \.settings\, \target\, \bin\, \HardHat\node_modules\, \prj1\artifacts\, \prj1\cache "
 

REM java DirectoryCopy.java source="D:/dennis" target="\\Dennisthinkcent\d\dennis" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_DennisThink_D_dennis_backup.log"  move_extra_files_to="\\Dennisthinkcent\d\temp\moved_files\delete_later\extra_files\d_to_d_dennis" exclude_files_dirs="\.git\, \.class, \.settings\, \target\, \bin\ "

java DirectoryCopy.java source="D:/dennis" target="E:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_E_dennis_backup.log"  move_extra_files_to="E:\temp\moved_files\delete_later\extra_files\d_to_e" exclude_files_dirs="\.git\, \.class, \.settings\, \target\, \bin\, D:\dennis\MiscLargeFiles\bt_data "

java DirectoryCopy.java source="D:\dennis\MiscLargeFiles\bt_data" target="\\TC01\d_on_t01\dennis\MiscLargeFiles\bt_data" log_file="C:\Users\admin\Desktop\Shortcuts\D_to_TC01_backup.log"  move_extra_files_to="\\TC01\d_on_t01\temp\moved_files\delete_later\extra_files\d_to_e" exclude_files_dirs="\.git\, \.class, \.settings\, \target\, \bin\ "



echo "All copying done"
pause