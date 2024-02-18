import tkinter as tk
import ttkbootstrap as ttk
import tkinter.filedialog, tkinter.messagebox
import random, os
from PIL import Image, ImageOps


class ImageKleaner(tk.Frame):
    def __init__(self, parent, *args, **kwargs):
        tk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.image_files = [] # list to store the image files path

        self.select_files_button = ttk.Button(self, text="Select image files", command=self.select_files_button_function)
        self.select_files_button.config(width=45)
        self.select_files_button.pack(padx=6, pady=6)

        self.clean_files_button = ttk.Button(self, text="Run image kleaner", command=self.clean_files_button_function)
        self.clean_files_button.config(width=45)
        self.clean_files_button.pack(padx=6, pady=6)

        self.listbox = tk.Listbox(self, height=14)                                            # Listbox widget to display the selected image files
        self.vscrollbar = tk.Scrollbar(self, orient='vertical', command=self.listbox.yview)   # Scrollbar widget for vertical scrolling
        self.hscrollbar = tk.Scrollbar(self, orient='horizontal', command=self.listbox.xview) # Scrollbar widget for horizontal scrolling
        self.listbox['yscrollcommand'] = self.vscrollbar.set                                  # configure the listbox to use the scrollbars
        self.listbox['xscrollcommand'] = self.hscrollbar.set
        self.vscrollbar.pack(side='right', fill='y')                                          # pack the scrollbars to the right and bottom of the frame
        self.hscrollbar.pack(side='bottom', fill='x')
        self.listbox.pack(fill='both', expand=True)                                           # pack the listbox to fill and expand the remaining space

    def select_files_button_function(self):
        self.image_files = list(tkinter.filedialog.askopenfilenames(filetypes=[("Image files", "*.jpg *.jpeg *.png")]))
        self.listbox.delete(0, tk.END) # delete all the items in the listbox
        for file in self.image_files:  # loop through the image files and insert them to the listbox
            self.listbox.insert(tk.END, file)

    def clean_files_button_function(self):
        cwd = os.getcwd()                           # get the current working directory
        if not self.image_files:                    # check if the image files list is empty
            tkinter.messagebox.showinfo("Info", "Please select the files that you want to clean.")
            return
        random_string = "".join(random.choices("0123456789abcdefghijklmnopqrstuvwxyz", k=5))
        folder_name = f"KleanFiles_{random_string}" # create a folder name using randomly generated string
        folder_path = os.path.join(cwd, folder_name)
        tkinter.messagebox.showwarning("Warning", "This may take some time depending on the number of files selected and the app window may be unresponsive.")
        if not os.path.exists(folder_path):         # check if the folder path does not exist
            os.mkdir(folder_path)
        for file in self.image_files:               # loop through the image files
            image = Image.open(file)
            image_copy = image.copy()
            image_copy = ImageOps.exif_transpose(image_copy)
            image_copy.getexif().clear()            # clear the EXIF data from the image copy
            file_name = os.path.basename(file)      # get the file name from the file path
            # save the image copy to the folder path with the same file name
            image_copy.save(os.path.join(folder_path, file_name))
            image.close()
            image_copy.close()
        tkinter.messagebox.showinfo("Info", f"Task completed, cleaned images are in \"{folder_path}\" folder.")


if __name__ == "__main__":
    root = tk.Tk()
    root.geometry("305x320")
    root.resizable(False, False)       # disable the resizing of the root window
    root.title("ImageKleaner")
    root.tk.call('tk', 'scaling', 1.2) # set the scaling factor of the root window to 1.2
    ImageKleaner(root).pack(side="top", fill="both", expand=True)
    root.mainloop()                    # start the main loop of the root window

