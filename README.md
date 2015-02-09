PxlSort
=======

Abstract
--------
Using a custom implementation of [radix sort](http://en.wikipedia.org/wiki/Radix_sort), PxlSort can sort pixels in a given image. The image is first loaded into a matrix and then it can be sorted in several different ways. Since it's a matrix, it can be sorted **vertically** and **horizontally**. Diagonal sorting is also in planning but it's in early alpha currently and it's disabled in the main program. 
Also, pixels can be sorted by their color values. They can be sorted by the amount *(0-255)* of **red**, **green** or **blue** color in them. By saving color values as bytes, radix sort is linear (*O(n)*) and lighting fast. 

Apart from pixel sorting, PxlSort can also **randomize** pixels in a given image and it can also **transpose** the given matrix.

If you wish to know more about the implementation, read my blog post about it here: [Pixel sorting - Introduction to glitch art](http://zx.rs/1/Pixel-sorting---Introduction-to-glitch-art/).


Usage
-----
PxlSort's main usage scenario is [glitch art](http://en.wikipedia.org/wiki/Glitch_art) generation, or [databending](http://en.wikipedia.org/wiki/Databending). By sorting pixels and/or randomizing their positions it can produce *magnificent* effects and artifacts.

Usage is really simple. Just load the image using **File menu** from the menu bar and you are set to go. Then, just choose the action you want to apply to the image and you'll see the image transform right in front of your eyes. If you're not happy with the results you can always use the Undo/Redo option from the **Edit menu**.


ToDos
-----
* Drag and drop implementation on the main canvas
* Tabs for manipulating multiple images

