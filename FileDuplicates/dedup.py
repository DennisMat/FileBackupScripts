import os
import hashlib

def get_file_size(file_path):
    """Get the size of the file in bytes."""
    return os.path.getsize(file_path)

def get_file_hash(file_path):
    """Calculate the hash of the file using SHA256."""
    hasher = hashlib.sha256()
    with open(file_path, 'rb') as f:
        buf = f.read()
        hasher.update(buf)
    return hasher.hexdigest()

def find_duplicates(directory):
    """Find and remove duplicate files in the given directory."""
    files_by_size = {} #dictionary of key/values
    duplicates = []
    originals=set()

    # Step 1: Group files by size
    for root, _, files in os.walk(directory):
        for file in files:
            file_path = os.path.join(root, file)
            if file_path.find(".git") == -1:
                file_size = get_file_size(file_path)
                if file_size in files_by_size:
                    files_by_size[file_size].append(file_path)
                else:
                    files_by_size[file_size] = [file_path]

    # Step 2: Check hashes for files with the same size
    for size, files in files_by_size.items():
        if len(files) > 1:
            duplicates.append("=========================")
            seen_hashes = {}
            for file in files:
                duplicates.append(file)
                file_hash = get_file_hash(file)
                if file_hash in seen_hashes:
                    # duplicates.append(file)
                    # duplicates.append(file + " hash:" + file_hash)
                    #duplicates.append(seen_hashes[file_hash])
                     print(f"Appeneded : {file} and {seen_hashes[file_hash]}")
                else:
                    seen_hashes[file_hash] = file



#      C:\dennis



    return originals,duplicates

def remove_duplicates(duplicates):
    """Remove duplicate files."""
    for file in duplicates:
        os.remove(file)
        print(f"Removed: {file}")

if __name__ == "__main__":
    #directory = input("Enter the directory to scan for duplicates: ")
    directory="C:\\dennis"
    originals,duplicates = find_duplicates(directory)
    if duplicates:
        print("Duplicate files found:")
        for file in duplicates:
            print(file)

        # remove_confirmation = input("Do you want to remove these files? (y/n): ")
        # if remove_confirmation.lower() == 'y':
        #     remove_duplicates(duplicates)
        # else:
        #     print("No files were removed.")
    else:
        print("No duplicate files found.")

    if originals:
        print("Originals:")
        for file in originals:
            print(file)