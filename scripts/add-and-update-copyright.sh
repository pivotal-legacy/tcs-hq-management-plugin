#!/bin/sh
commitMessage="Update copyright headers"

dir=`dirname $0`
dir=`cd $dir;pwd -P`

template="$dir/header.awk"

for f in `find . -type f -name "*.java" -or -type f -name "*.groovy" -or -name "*.sh" -or -name "*.bat" -or -name "*.properties" -or -name "mbeans-descriptors.xml" | grep -v -E '(scripts|ivy-cache|spring-build|integration-repo|download-staging)'`
do
	echo $f
	
	currentyear=
	outputyear=
	years=

	gitLog=`git log --date=short --follow --format=%cd%H $f`
	
	if [ -z "$gitLog" ] 
	then
		gitLog=`git log --date=short --format=%cd%H $f`
	fi
	
	for log in $gitLog
	do
		year=`echo $log | cut -c -4`
		commitHash=`echo $log | cut -c 11-`
		message=`git show -s --format=%s $commitHash`
		
		copyrightHeaderCommit=`echo $message | grep --count "$commitMessage"`
		
		if [ $copyrightHeaderCommit -lt 1 ]
		then
			if [ -z $currentyear ]
			then
				currentyear=$year
				outputyear=$year
				years=$year
			elif [ $year -eq $currentyear ]
			then
				year=$currentyear
			elif [ `expr $year + 1` -eq $currentyear ]
			then
				currentyear=$year
			else
				if [ $currentyear -eq $outputyear]
				then
					years="$year, $years"
				else
					years="$year, $currentyear-$years"
				fi
				outputyear=$year
				currentyear=$year
			fi
		fi
	done

	if [ $currentyear -ne $outputyear ]
	then
		years="$currentyear-$years"
	fi

	suffix=${f##*.}
	
	if [ "$suffix" == "sh" -o "$suffix" == "properties" ]
	then
		copyright="# Copyright \\(c\\) $years VMware, Inc.  All rights reserved."
		sed -ie s/"^.*# Copyright.*$"/"$copyright"/ $f
		rm $f"e"
	elif [ "$suffix" == "bat" ]
	then
		copyright="rem Copyright \\(c\\) $years VMware, Inc.  All rights reserved."
		sed -ie s/"^.*rem Copyright.*$"/"$copyright"/ $f
		rm $f"e"
	elif [ "$suffix" == "xml" ]
	then
		copyright="<!-- Copyright \\(c\\) $years VMware, Inc.  All rights reserved. -->"
		sed -ie s/"^.*<!-- Copyright.*$"/"$copyright"/ $f
		rm $f"e"
	else
		copyright="// Copyright (c) $years VMware, Inc.  All rights reserved."
		fin=$f
		fout="$f.out"
		fbak="$f.bak"

		awk -f $template -v copyright="$copyright" $fin > $fout
		mv $fout $fin
	fi
done

git commit -a -m "$commitMessage"