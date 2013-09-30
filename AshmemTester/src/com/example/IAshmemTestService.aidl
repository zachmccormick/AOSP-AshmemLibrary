package com.example;

import edu.vanderbilt.mccormick.ashmemlibrary.AshmemBuffer;

interface IAshmemTestService {
	void work(in AshmemBuffer b);
}