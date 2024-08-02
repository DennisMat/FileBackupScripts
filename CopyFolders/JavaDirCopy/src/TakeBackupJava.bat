echo "Started copying C:/dennis to D:/dennis" 
java DirectoryCopy.java "C:/dennis" "D:/dennis" "C:\Users\admin\Desktop\Shortcuts\backup.log" ".git" ".class" ".settings" "target" "bin"
echo "Done copying C:/dennis to D:/dennis" 

echo "Started copying D:/dennis to \\Dennisthinkcent\d\dennis"
java DirectoryCopy.java "D:/dennis" "\\Dennisthinkcent\d\dennis" "C:\Users\admin\Desktop\Shortcuts\backup.log" ".git" ".class" ".settings" "target" "bin"
pause