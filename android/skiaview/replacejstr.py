#!/usr/bin/env python
import os
file = os.path.abspath('android/skiaview/jni/skiaview_java_wrap.cpp')
text = open(file).read()
text = text.replace('jstring jname = 0 ;', 'jstring jname = 0; TmpJOBJ jtmp(jenv, &jname);')
open(file, 'w').write(text)
