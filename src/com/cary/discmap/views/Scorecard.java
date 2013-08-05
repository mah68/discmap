package com.cary.discmap.views;

public class Scorecard {
	public int[] scores;
	public int courseId;
	public String courseName;
	public String timeStamp;
	public int overallScore;
	
	public Scorecard(int[] scores, int courseId, String courseName, String timeStamp) {
		this.scores = scores;
		this.courseId = courseId;
		this.courseName = courseName;
		this.timeStamp = timeStamp;
		this.overallScore = getOverall();
	}
	
	private int getOverall() {
		int score = 0;
		for(int s : scores) {
			score += s;
		}
		return score;
	}
	
	public boolean containsCourse(String course) {
		course = course.toLowerCase();
		String[] queries = course.split(" ");
		for(String query : queries) {
			if(!courseName.toLowerCase().contains(query)) {
				return false;
			}
		}
		return true;
	}

}
