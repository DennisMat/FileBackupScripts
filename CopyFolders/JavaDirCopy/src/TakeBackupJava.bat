
java DirectoryCopy.java source="C:/dennis" target="D:/dennis" log_file="C:\Users\admin\Desktop\Shortcuts\backup.log" exclude_files_dirs=".git, .class, .settings, target, bin"
 

java DirectoryCopy.java source="D:/dennis" target="\\Dennisthinkcent\d\dennis" log_file="C:\Users\admin\Desktop\Shortcuts\backup.log" move_extra_files_to="\\Dennisthinkcent\d\temp\delete_later\extra_files" exclude_files_dirs=".git, .class, .settings, target, bin"

echo "All copying done"
pause