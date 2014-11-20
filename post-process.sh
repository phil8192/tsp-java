#!/bin/bash
# example: ./post-process.sh /tmp/out.points
# ^ dumps visualisation to /tmp/out.png
gnuplot -e "unset border; unset xtics; unset ytics;
            set term pngcairo size 1600,1600 enhanced; set output '/tmp/out.png'; 
            set label ""; set key off; set obj 1 rectangle behind from screen 0,0 to screen 1,1; 
            set obj 1 fillstyle solid 1.0 fillcolor rgbcolor '#ffffff'; 
            plot '$1' using 1:2 with lines lw 1.0 lc rgb '#000000'"

#convert /tmp/out.png -flip /tmp/out-flip.png
#convert /tmp/out-flip.png -rotate -90 $2

