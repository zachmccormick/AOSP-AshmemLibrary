AshmemLibrary
=============

A library for creating Anonymous Shared Memory buffers in Java that can be passed around to different apps/processes via services.

Just add AshmemLibrary as a library project to your app and start using it.  Note that the factory method takes the number of pages you want, not the number of bytes.  I will update this if people want some other cool features (like pinning, unpinning memory, etc.).  I could probably also create an observer pattern on top of this so that apps are notified when the pool is modified... let me know what would be cool @ zach.mccormick@vanderbilt.edu!

A fun fact is that you can reserve WAY more RAM than the application VM heap limit.  Normally you might be able to reserve 32 or 64 MB of RAM per application.  With this, I was able to successfully grab 512 MB of RAM on a Nexus S running stock AOSP.  I don't know if this is a feature or a possible exploit avenue.
